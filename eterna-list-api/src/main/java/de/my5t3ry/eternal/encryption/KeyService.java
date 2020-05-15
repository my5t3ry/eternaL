package de.my5t3ry.eternal.encryption;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * User: my5t3ry
 * Date: 5/15/20 4:50 AM
 */
public class KeyService {

    private static final String ALGORITHM = "RSA";

    private static byte[] encrypt(byte[] publicKey, byte[] inputData) throws Exception {
        PublicKey key = KeyFactory.getInstance(ALGORITHM)    /* ExceptionL Invalid DER encoding */
                .generatePublic(new X509EncodedKeySpec(publicKey));
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.PUBLIC_KEY, key);
        return cipher.doFinal(inputData);
    }

    public static String encrypt(String publicKey, String inputData) throws Exception {
        return bytesToEncodedString(encrypt(encodedStringToBytes(publicKey), stringToBytes(inputData)));
    }

    private static byte[] decrypt(byte[] privateKey, byte[] inputData) throws Exception {
        PrivateKey key = KeyFactory.getInstance(ALGORITHM)
                .generatePrivate(new PKCS8EncodedKeySpec(privateKey));
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(inputData);
    }

    public static String decrypt(String privateKey, String inputData) throws Exception {
        return bytesToString(decrypt(
                encodedStringToBytes(privateKey),
                encodedStringToBytes(inputData)));
    }

    public static KeyPair generateKeyPair()
            throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
        keyGen.initialize(2048);
        return keyGen.generateKeyPair();
    }

    private static String bytesToString(byte[] bytes) {
        return new String(bytes);
    }

    private static byte[] stringToBytes(String astring) {
        return astring.getBytes();
    }

    private static String bytesToEncodedString(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    private static byte[] encodedStringToBytes(String encodedString) {
        return Base64.getDecoder().decode(encodedString);
    }

    public static StringKeyPair convertToStringKeyPair(KeyPair keyPair) {
        byte[] publicKey = keyPair.getPublic().getEncoded();
        byte[] privateKey = keyPair.getPrivate().getEncoded();
        return new StringKeyPair(bytesToEncodedString(publicKey), bytesToEncodedString(privateKey));
    }


    public static StringKeyPair generateStringKeyPair() {
        try {
            return KeyService.convertToStringKeyPair(KeyService.generateKeyPair());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new IllegalStateException("could not generate key", e);
        }
    }
}
