package gdsmartcard.globalplatform;

import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

import gdsmartcard.ISO7816;

public class SecureCardChannel extends CardChannel {

    private static final byte LENGTH_CMAC            = 8;
    private CardChannel       channel;
    private SecretKey         encSessionKey;
    private SecretKey         macSessionKey;
    private SecretKey         dekSessionKey;
    private byte[]            hostChallenge;
    private byte[]            icv;
    private byte[]            keyDiversificationData = new byte[10];
    private byte[]            cardChallenge          = new byte[6];
    private byte[]            cardCryptogram         = new byte[8];
    private Integer           keyVersionNumber;
    private Integer           protocolIdentifier;
    private Integer           sequenceCounter;

    private SecureCardChannel(CardChannel channel) {
        this.channel = channel;
    }

    public static SecureCardChannel initializeUpdate(CardChannel channel) throws CardException {
        byte[] hostChallenge = new byte[8];
        new Random().nextBytes(hostChallenge);
        return initializeUpdate(channel, hostChallenge);
    }

    public static SecureCardChannel initializeUpdate(CardChannel channel, byte[] hostChallenge) throws CardException {
        SecureCardChannel secureChannel = new SecureCardChannel(channel);
        secureChannel.hostChallenge = hostChallenge.clone();

        CommandAPDU initUpdate = GlobalPlatform.initializeUpateCommand(hostChallenge);
        ResponseAPDU response = secureChannel.transmit(initUpdate);
        if (response.getSW() != 0x9000) {
            throw new CardException(String.format("INITIALIZE UPDATE failed with SW 0x%04X.", response.getSW()));
        }

        ByteBuffer responseBuffer = ByteBuffer.wrap(response.getData());
        responseBuffer.get(secureChannel.keyDiversificationData);
        secureChannel.keyVersionNumber = responseBuffer.get() & 0xff;
        secureChannel.protocolIdentifier = responseBuffer.get() & 0xff;
        secureChannel.sequenceCounter = responseBuffer.getShort() & 0xffff;
        responseBuffer.get(secureChannel.cardChallenge).get(secureChannel.cardCryptogram);

        return secureChannel;
    }

    public void externalAuthenticate(int securityLevel) throws CardException {
        try {
            byte[] hostCryptogram = calculateHostCryptogram();
            CommandAPDU externalAuthenticate = GlobalPlatform.externalAuthenticateCommand(securityLevel, hostCryptogram);
            ResponseAPDU response = channel.transmit(appendCommandMAC(externalAuthenticate));
            if (response.getSW() != 0x9000) {
                throw new CardException(String.format("EXTERNAL AUTHENTICATE failed with SW 0x%04X.", response.getSW()));
            }
        } catch (GeneralSecurityException e) {
            throw new CardException(e);
        }

    }

    public static SecureCardChannel open(GlobalPlatformCardChannel channel, int securityLevel) throws CardException {
        try {
            SecureCardChannel secureChannel = SecureCardChannel.initializeUpdate(channel);

            SecureChannelKeySet keySet = channel.getKeySet(secureChannel.keyVersionNumber);
            if (keySet == null) {
                throw new CardException(String.format("Missing key set: version %02X", secureChannel.keyVersionNumber));
            }

            if (!secureChannel.verifyCardCryptogram()) {
                throw new CardException("Invalid card cryptogram.");
            }

            secureChannel.externalAuthenticate(securityLevel);
            return secureChannel;
        } catch (GeneralSecurityException e) {
            throw new CardException(e);
        }
    }

    public static SecureCardChannel open(CardChannel channel, SecureChannelKeySet keys, int securityLevel) throws CardException {
        try {
            SecureCardChannel secureChannel = SecureCardChannel.initializeUpdate(channel);

            if (!secureChannel.verifyCardCryptogram()) {
                throw new CardException("Invalid card cryptogram.");
            }

            secureChannel.externalAuthenticate(securityLevel);
            return secureChannel;
        } catch (GeneralSecurityException e) {
            throw new CardException(e);
        }
    }

    @Override
    public void close() throws CardException {
        channel.close();
    }

    @Override
    public Card getCard() {
        return channel.getCard();
    }

    @Override
    public int getChannelNumber() {
        return channel.getChannelNumber();
    }

    @Override
    public ResponseAPDU transmit(CommandAPDU arg0) throws CardException {
        return channel.transmit(arg0);
    }

    @Override
    public int transmit(ByteBuffer arg0, ByteBuffer arg1) throws CardException {
        return channel.transmit(arg0, arg1);
    }

    public CommandAPDU appendCommandMAC(CommandAPDU command) throws GeneralSecurityException {
        CommandAPDU modifiedCommand;

        // Le byte is not included in MAC calculation
        ByteBuffer b = ByteBuffer.allocate(ISO7816.OFFSET_CDATA + command.getData().length);
        b.put((byte) (command.getCLA() | 0x04));
        b.put((byte) command.getINS());
        b.put((byte) command.getP1());
        b.put((byte) command.getP2());
        b.put((byte) (command.getNc() + LENGTH_CMAC));
        b.put(command.getData());

        // calculate the MAC and update the ICV
        byte[] mac = calculateCommandMAC(b.array());
        icv = mac;

        // return the modified command with Le if any
        if (command.getNe() == 0) {
            modifiedCommand = new CommandAPDU(ByteBuffer.allocate(b.array().length + mac.length)
                    .put(b.array()).put(mac).array());
        } else {
            byte ne = (byte) ((command.getNe() == 256) ? 0 : command.getNe());
            modifiedCommand = new CommandAPDU(ByteBuffer.allocate(b.array().length + mac.length + 1)
                    .put(b.array()).put(mac).put(ne).array());
        }

        return modifiedCommand;
    }

    /**
     * The generation and verification of the card cryptogram is performed by concatenating the 8-byte host challenge,
     * 2-byte Sequence Counter, and 6-byte card challenge resulting in a 16-byte block.
     * <p>
     * Applying the same padding rules defined in Appendix B.4 - DES Padding, the data shall be padded with a further
     * 8-byte block ('80 00 00 00 00 00 00 00').
     * <p>
     * The signature method, using the S-ENC session key and an ICV of binary zeroes, is applied across this 24-byte
     * block and the resulting 8-byte signature is the card cryptogram.
     * 
     * @throws GeneralSecurityException
     * @return <code>true</code> if the verification was successful, <code>false</code> otherwise
     */
    public boolean verifyCardCryptogram() throws GeneralSecurityException {
        ByteBuffer challengeBuffer = ByteBuffer.allocate(24);
        challengeBuffer.put(hostChallenge);
        challengeBuffer.putShort(sequenceCounter.shortValue());
        challengeBuffer.put(cardChallenge);
        challengeBuffer.put(new byte[] { (byte) 0x80, 0, 0, 0, 0, 0, 0, 0 });
        Cipher cipher = Cipher.getInstance("DESede/CBC/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, encSessionKey, new IvParameterSpec(new byte[8]));
        byte[] cipherText = cipher.doFinal(challengeBuffer.array());

        // the last eight bytes of the cipher text are the signature
        return Arrays.equals(cardCryptogram, Arrays.copyOfRange(cipherText, cipherText.length - 8, cipherText.length));
    }

    /**
     * The generation and verification of the host cryptogram is performed by concatenating the 2-byte Sequence Counter,
     * 6-byte card challenge, and 8-byte host challenge resulting in a 16-byte block.
     * <p>
     * Applying the same padding rules defined in Appendix B.4 - DES Padding, the data shall be padded with a further
     * 8-byte block ('80 00 00 00 00 00 00 00').
     * <p>
     * The signature method, using the S-ENC session key and an ICV of binary zeroes, is applied across this 24-byte
     * block and the resulting 8-byte signature is the host cryptogram.
     * 
     * @return the host cryptogram
     * @throws GeneralSecurityException
     */
    public byte[] calculateHostCryptogram() throws GeneralSecurityException {
        ByteBuffer challengeBuffer = ByteBuffer.allocate(24);
        challengeBuffer.putShort(sequenceCounter.shortValue());
        challengeBuffer.put(cardChallenge);
        challengeBuffer.put(hostChallenge);
        challengeBuffer.put(new byte[] { (byte) 0x80, 0, 0, 0, 0, 0, 0, 0 });
        Cipher cipher = Cipher.getInstance("DESede/CBC/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, encSessionKey, new IvParameterSpec(new byte[8]));
        byte[] cipherText = cipher.doFinal(challengeBuffer.array());

        // the last eight bytes of the cipher text are the signature
        return Arrays.copyOfRange(cipherText, cipherText.length - 8, cipherText.length);
    }

    private static byte[] padIso97971M2(byte[] in) {
        // pad data according to ISO 9797-1 method 2
        ByteBuffer dataBuffer = ByteBuffer.allocate(in.length + 8);
        dataBuffer.put(in).put((byte) 0x80);
        while (dataBuffer.position() % 8 != 0) {
            dataBuffer.put((byte) 0);
        }
        byte[] out = new byte[dataBuffer.position()];
        dataBuffer.rewind();
        dataBuffer.get(out);

        return out;
    }

    private byte[] calculateCommandMAC(byte[] commandBytes) throws GeneralSecurityException {
        // encrypt ICV (not for first command in session)
        if (icv == null) {
            icv = new byte[8];
        } else {
            Cipher icvCipher = Cipher.getInstance("DES/ECB/NoPadding");
            icvCipher.init(Cipher.ENCRYPT_MODE, macSessionKey);
            icv = icvCipher.doFinal(icv);
        }

        IvParameterSpec ivSpec = new IvParameterSpec(icv);
        Cipher cipher = Cipher.getInstance("DES/CBC/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, macSessionKey, ivSpec);
        byte[] cipherText = cipher.doFinal(padIso97971M2(commandBytes));
        byte[] mac = Arrays.copyOfRange(cipherText, cipherText.length - 8,
                cipherText.length);

        SecretKey key1 = SecretKeyFactory.getInstance("DESede").generateSecret(new DESedeKeySpec(Arrays.copyOf(macSessionKey.getEncoded(), 8)));
        SecretKey key2 = SecretKeyFactory.getInstance("DESede").generateSecret(new DESedeKeySpec(Arrays.copyOfRange(macSessionKey.getEncoded(), 8, 16)));
        cipher.init(Cipher.DECRYPT_MODE, key2, ivSpec);
        mac = cipher.doFinal(mac);
        cipher.init(Cipher.ENCRYPT_MODE, key1, ivSpec);
        mac = cipher.doFinal(mac);

        return mac;
    }
}
