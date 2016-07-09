package gdsmartcard.globalplatform;

import java.nio.ByteBuffer;
import java.util.Random;

import javax.smartcardio.CardChannel;
import javax.smartcardio.CommandAPDU;

import gdsmartcard.AID;

/**
 * Provides access to a GlobalPlatform cards.
 */
public class GlobalPlatform {

    /** The CLA byte of an ISO7816 command. */
    public static final int CLA_ISO = 0x00;

    /** The CLA byte of a GlobalPlatform command. */
    public static final int CLA_GLOBALPLATFORM = 0x80;

    /** The INS byte of a SELECT command. */
    public static final int INS_SELECT = 0xA4;

    /** The INS byte of a DELETE command. */
    public static final int INS_DELETE = 0xE4;

    /** The INS byte of a SET STAUTS command. */
    public static final int INS_SET_STATUS = 0xF0;

    /** The INS byte of a GET DATA command. */
    public static final int INS_GET_DATA = 0xCA;

    /** The INS byte of a GET STATUS command. */
    public static final int INS_GET_STATUS = 0xF2;

    /** The INS byte of an INITIALIZE UPDATE command. */
    public static final int INS_INITIALIZE_UPDATE = 0x50;

    /** The INS byte of an EXTERNAL AUTHENTICATE command. */
    public static final int INS_EXTERNAL_AUTHENTICATE = 0x82;

    /** The reference control parameter P1 of a SELECT command meaning 'select by name'. */
    public static final int P1_SELECT_BY_NAME = 0x04;

    /**
     * The reference control parameter P1 - status type of a SET STATUS command meaning 'Issuer Security Domain only'.
     */
    public static final int P1_SET_STATUS_ISD = 0x80;

    /**
     * The reference control parameter P1 - status type of a SET STATUS command meaning 'Applications and Supplementary
     * Security Domains only'.
     */
    public static final int P1_SET_STATUS_APPLICATION = 0x40;

    /** The reference control parameter P1 of a DELETE command. */
    public static final int P1_DELETE = 0x00;

    /** The reference control parameter P1 of a GET STATUS command meaning ' Issuer Security Domain only'. */
    public static final int P1_GET_STATUS_ISD_ONLY = 0x80;

    /**
     * The reference control parameter P1 of a GET STATUS command meaning 'Applications and Supplementary Security
     * Domains only'.
     */
    public static final int P1_GET_STATUS_APPLICATIONS = 0x40;

    /**
     * The reference control parameter P1 of a GET STATUS command meaning 'Executable Load Files only'.
     */
    public static final int P1_GET_STATUS_EXECUTABLE_LOAD_FILES = 0x20;

    /** The reference control parameter P2 of a DELETE command meaning 'delete object'. */
    public static final int P2_DELETE_OBJECT = 0x00;

    /** The reference control parameter P2 of a DELETE command meaning 'delete object and related'. */
    public static final int P2_DELETE_OBJECT_AND_RELATED = 0x80;

    /** The reference control parameter P2 of a SELECT command meaning 'first or only occurrence'. */
    public static final int P2_SELECT_FIRST_OR_ONLY = 0x00;

    /** The reference control parameter P2 of a SELECT command meaning 'next occurrence'. */
    public static final int P2_SELECT_NEXT = 0x02;

    /** The reference control parameter P2 of a GET STATUS command meaning 'get first or all occurrence(s)'. */
    public static final int P2_GET_STATUS_FIRST_OR_ALL = 0x00;

    /** The reference control parameter P2 of a GET STATUS command meaning 'get next occurrence'. */
    public static final int P2_GET_STATUS_NEXT = 0x01;

    private CardChannel channel;

    private static CommandAPDU selectCommand(int p2, AID aid) {
        if (aid != null)
            return new CommandAPDU(CLA_ISO, INS_SELECT, P1_SELECT_BY_NAME, p2, aid.toBytes());
        else
            return new CommandAPDU(CLA_ISO, INS_SELECT, P1_SELECT_BY_NAME, p2, 256);
    }

    public static CommandAPDU selectFirstOrOnlyCommand(AID aid) {
        return selectCommand(P2_SELECT_FIRST_OR_ONLY, aid);
    }

    public static CommandAPDU selectNextCommand(AID aid) {
        return selectCommand(P2_SELECT_NEXT, aid);
    }

    public static CommandAPDU setStatusOfISDCommand(ISDLifeCycleState state) {
        return new CommandAPDU(CLA_GLOBALPLATFORM, INS_SET_STATUS, P1_SET_STATUS_ISD, state.getValue());
    }

    public static CommandAPDU setStatusOfApplicationCommand(ApplicationLifeCycleState state, AID aid) {
        byte[] aidBytes = (aid != null) ? aid.toBytes() : new byte[0];
        return new CommandAPDU(CLA_GLOBALPLATFORM, INS_SET_STATUS, P1_SET_STATUS_APPLICATION, state.getValue(), aidBytes);
    }

    public static CommandAPDU getDataCommand(ISDTag tag) {
        return getDataCommand(tag.getValue());
    }

    public static CommandAPDU getDataCommand(int tag) {
        return new CommandAPDU(CLA_GLOBALPLATFORM, INS_GET_DATA, tag >> 8, tag & 0xFF);
    }

    /**
     * Returns a DELETE command with the given options.
     * <p>
     * The DELETE command is used to delete a uniquely identifiable object such as an Executable Load File, an
     * Application, an Executable Load File and its related Applications or a key. In order to delete an object, the
     * object shall be uniquely identifiable by the selected Application.
     * 
     * @param deleteRelated
     *            indicates whether the object in the data field is being deleted or whether the object in the data
     *            field and its related objects are being deleted
     * @param objects
     *            the TLV coded name(s) of the object to be deleted
     * @return a DELETE command with the given options
     */
    public static CommandAPDU deleteCommand(boolean deleteRelated, TLVObject... objects) {
        int p2 = deleteRelated ? P2_DELETE_OBJECT_AND_RELATED : P2_DELETE_OBJECT;

        int lc = 0;
        for (TLVObject o : objects) {
            lc += o.getSize();
        }

        ByteBuffer commandDataBuffer = ByteBuffer.allocate(lc);
        for (TLVObject o : objects) {
            commandDataBuffer.put(o.array());
        }

        return new CommandAPDU(CLA_GLOBALPLATFORM, INS_DELETE, P1_DELETE, p2, commandDataBuffer.array());
    }

    public static CommandAPDU deleteCardContentCommand(boolean deleteRelated, AID aid) {
        return deleteCommand(deleteRelated, new TLVObject(0x4F, aid.toBytes()));
    }

    public static CommandAPDU deleteKeyCommand(boolean deleteRelated, int identifier, int versionNumber) {
        return deleteCommand(deleteRelated, new TLVObject(0xD0, new byte[] { (byte) identifier }), new TLVObject(0xD2, new byte[] { (byte) versionNumber }));
    }

    public static CommandAPDU getStatusCommand(int p1, int p2) {
        return getStatusCommand(p1, p2, (byte[]) null);
    }

    public static CommandAPDU getStatusCommand(int p1, int p2, AID aid) {
        return getStatusCommand(p1, p2, (aid != null) ? aid.toBytes() : (byte[]) null);
    }

    public static CommandAPDU getStatusCommand(int p1, int p2, byte[] data) {
        if (data == null) {
            data = new byte[0];
        }
        ByteBuffer dataBuffer = ByteBuffer.allocate(2 + data.length).put((byte) 0x4F).put((byte) data.length).put(data);
        return new CommandAPDU(CLA_GLOBALPLATFORM, INS_GET_STATUS, p1, p2, dataBuffer.array());
    }

    public static CommandAPDU initializeUpateCommand() {
        byte[] hostChallenge = new byte[8];
        new Random().nextBytes(hostChallenge);
        return initializeUpateCommand(0, hostChallenge);
    }

    public static CommandAPDU initializeUpateCommand(byte[] hostChallenge) {
        return initializeUpateCommand(0, hostChallenge);
    }

    public static CommandAPDU initializeUpateCommand(int keyVersionNumber, byte[] hostChallenge) {
        return new CommandAPDU(GlobalPlatform.CLA_GLOBALPLATFORM, GlobalPlatform.INS_INITIALIZE_UPDATE,
                keyVersionNumber, 0, hostChallenge);
    }

    public static CommandAPDU externalAuthenticateCommand(int securityLevel, byte[] hostCryptogram) {
        return new CommandAPDU(GlobalPlatform.CLA_GLOBALPLATFORM, INS_EXTERNAL_AUTHENTICATE, securityLevel, 0, hostCryptogram);
    }

    public static CommandAPDU installForLoad(AID aid) {
        // TODO Auto-generated method stub
        return null;
    }

    public static CommandAPDU loadCommand(byte[] nextLoadBlock, int i, boolean isLastBlock) {
        // TODO Auto-generated method stub
        return null;
    }
}
