package gdsmartcard;

public class HexUtils {

    public static byte[] toBytes(String hexString) {
        hexString = hexString.toUpperCase().replaceAll("\\s", "").replaceAll("0X", "").replaceAll("\\W", "");
        hexString = (hexString.length() % 2 == 0) ? hexString : "0" + hexString;

        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0, j = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hexString.substring(j, j + 2), 16);
            j += 2;
        }

        return bytes;
    }

    public static String toHexString(byte[] bytes) {
        StringBuilder s = new StringBuilder();
        for (byte b : bytes) {
            s.append(String.format("%02X ", b));
        }

        return s.toString().trim();
    }

}
