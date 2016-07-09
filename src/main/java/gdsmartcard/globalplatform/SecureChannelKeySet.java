package gdsmartcard.globalplatform;

import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import gdsmartcard.HexUtils;

public class SecureChannelKeySet {

    public static SecureChannelKeySet VISA_TEST_KEYS;
    private static final byte[]       VISA_KEY_DATA = HexUtils.toBytes("404142434445464748494A4B4C4D4E4F");

    static {
        try {
            VISA_TEST_KEYS = new SecureChannelKeySet(VISA_KEY_DATA, VISA_KEY_DATA, VISA_KEY_DATA);
        } catch (GeneralSecurityException e) {
            // won't happen
        }
    }

    private SecretKey enc;
    private SecretKey mac;
    private SecretKey dek;

    public SecureChannelKeySet(byte[] enc, byte[] mac, byte[] dek) throws GeneralSecurityException {
        this.enc = toKey(enc);
        this.mac = toKey(mac);
        this.dek = toKey(dek);
    }

    private SecretKey toKey(byte[] keyData) throws GeneralSecurityException {
        if (keyData.length == 16) {
            keyData = ByteBuffer.allocate(24).put(keyData).put(keyData, 0, 8).array();
        }
        return SecretKeyFactory.getInstance("DESede").generateSecret(new DESedeKeySpec(keyData));
    }

}
