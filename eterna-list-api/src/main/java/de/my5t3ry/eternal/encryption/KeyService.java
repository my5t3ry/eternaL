package de.my5t3ry.eternal.encryption;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.IOException;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

/**
 * User: my5t3ry
 * Date: 5/15/20 4:50 AM
 */
public class KeyService {

    private static final String ALGORITHM = "RSA";
    private static final String PRIVATE_KEY_ENCRYPTION_ALGORITHM = "PBEWithSHA1AndDESede";
    private static final byte[] SALT = new byte[8];
    private static final int SALT_ITERATION_COUNT = 20;

    private static byte[] encrypt(byte[] publicKey, byte[] inputData) throws Exception {
        PublicKey key = KeyFactory.getInstance(ALGORITHM)
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

    public static String decrypt(String privateKey, String inputData, String secret) throws Exception {
        return bytesToString(decrypt(
                decodePrivateKey(encodedStringToBytes(privateKey), secret),
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

    private static byte[] stringToBytes(String string) {
        return string.getBytes();
    }

    private static String bytesToEncodedString(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    private static byte[] encodedStringToBytes(String encodedString) {
        return Base64.getDecoder().decode(encodedString);
    }

    public static StringKeyPair convertToStringKeyPair(KeyPair keyPair, String secret) {
        try {
            byte[] publicKey = keyPair.getPublic().getEncoded();
            byte[] privateKey = encryptPrivateKey(keyPair.getPrivate().getEncoded(), secret.toCharArray());
            return new StringKeyPair(bytesToEncodedString(publicKey), bytesToEncodedString(privateKey));
        } catch (IOException
                | NoSuchAlgorithmException
                | InvalidKeySpecException
                | NoSuchPaddingException
                | BadPaddingException
                | IllegalBlockSizeException
                | InvalidAlgorithmParameterException
                | InvalidKeyException
                | InvalidParameterSpecException e) {
            throw new IllegalStateException(String.format("could not convert key pair ['%s'].", e.getMessage()), e);
        }
    }

    private static byte[] decodePrivateKey(byte[] privateKey, String secret) {
        try {
            EncryptedPrivateKeyInfo encryptPKInfo = new EncryptedPrivateKeyInfo(privateKey);
            Cipher cipher = Cipher.getInstance(encryptPKInfo.getAlgName());
            PBEKeySpec pbeKeySpec = new PBEKeySpec(secret.toCharArray());
            SecretKeyFactory secFac = SecretKeyFactory.getInstance(encryptPKInfo.getAlgName());
            Key pbeKey = secFac.generateSecret(pbeKeySpec);
            AlgorithmParameters algParams = encryptPKInfo.getAlgParameters();
            cipher.init(Cipher.DECRYPT_MODE, pbeKey, algParams);
            KeySpec pkcs8KeySpec = encryptPKInfo.getKeySpec(cipher);
            KeyFactory kf = KeyFactory.getInstance(ALGORITHM);
            return kf.generatePrivate(pkcs8KeySpec).getEncoded();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] encryptPrivateKey(byte[] data, char[] password) throws
            IOException,
            NoSuchAlgorithmException,
            InvalidKeySpecException,
            NoSuchPaddingException,
            BadPaddingException,
            IllegalBlockSizeException,
            InvalidAlgorithmParameterException,
            InvalidKeyException,
            InvalidParameterSpecException {
        SecureRandom random = new SecureRandom();
        random.nextBytes(SALT);
        PBEKeySpec pbeKeySpec = new PBEKeySpec(password);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(PRIVATE_KEY_ENCRYPTION_ALGORITHM);
        SecretKey pbeKey = keyFactory.generateSecret(pbeKeySpec);
        Cipher pbeCipher = Cipher.getInstance(PRIVATE_KEY_ENCRYPTION_ALGORITHM);
        pbeCipher.init(Cipher.ENCRYPT_MODE, pbeKey, new PBEParameterSpec(SALT, SALT_ITERATION_COUNT));
        byte[] cipherText = pbeCipher.doFinal(data);
        AlgorithmParameters algorithmParameters = AlgorithmParameters.getInstance(PRIVATE_KEY_ENCRYPTION_ALGORITHM);
        algorithmParameters.init(new PBEParameterSpec(SALT, SALT_ITERATION_COUNT));
        return new EncryptedPrivateKeyInfo(algorithmParameters, cipherText).getEncoded();
    }

    public static StringKeyPair generateStringKeyPair(final String secret) {
        try {
            return KeyService.convertToStringKeyPair(KeyService.generateKeyPair(), secret);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new IllegalStateException("could not generate key", e);
        }
    }
}
