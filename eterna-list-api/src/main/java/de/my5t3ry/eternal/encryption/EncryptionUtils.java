package de.my5t3ry.eternal.encryption;

import java.util.Base64;

/**
 * User: my5t3ry
 * Date: 5/15/20 7:13 PM
 */
public class EncryptionUtils {


    public static String bytesToString(byte[] bytes) {
        return new String(bytes);
    }

    public static byte[] stringToBytes(String string) {
        return string.getBytes();
    }

    public static String bytesToEncodedString(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static byte[] encodedStringToBytes(String encodedString) {
        return Base64.getDecoder().decode(encodedString);
    }

}
