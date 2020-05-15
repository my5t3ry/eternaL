package de.my5t3ry.eternal.encryption;

import de.my5t3ry.eternal.jwt.JwtAuthentication;
import de.my5t3ry.eternal.list.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.Objects;

import static de.my5t3ry.eternal.encryption.EncryptionUtils.bytesToEncodedString;

/**
 * User: my5t3ry
 * Date: 5/15/20 4:50 AM
 */
@Component
public class EncryptionService {

    private final String DEFAULT_KEY_ALGORITHM = "RSA";
    private final String PRIVATE_KEY_ENCRYPTION_ALGORITHM = "PBEWithSHA1AndDESede";
    private final byte[] SALT = new byte[8];
    private final int SALT_ITERATION_COUNT = 20;

    @Autowired
    private StringKeyPairRepository stringKeyPairRepository;

    public KeyPair generateKeyPair()
            throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(DEFAULT_KEY_ALGORITHM);
        keyGen.initialize(2048);
        return keyGen.generateKeyPair();
    }

    private byte[] encryptPrivateKey(byte[] data, char[] secret)
            throws IOException,
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
        PBEKeySpec pbeKeySpec = new PBEKeySpec(secret);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(PRIVATE_KEY_ENCRYPTION_ALGORITHM);
        SecretKey pbeKey = keyFactory.generateSecret(pbeKeySpec);
        Cipher pbeCipher = Cipher.getInstance(PRIVATE_KEY_ENCRYPTION_ALGORITHM);
        pbeCipher.init(Cipher.ENCRYPT_MODE, pbeKey, new PBEParameterSpec(SALT, SALT_ITERATION_COUNT));
        byte[] cipherText = pbeCipher.doFinal(data);
        AlgorithmParameters algorithmParameters = AlgorithmParameters.getInstance(PRIVATE_KEY_ENCRYPTION_ALGORITHM);
        algorithmParameters.init(new PBEParameterSpec(SALT, SALT_ITERATION_COUNT));
        return new EncryptedPrivateKeyInfo(algorithmParameters, cipherText).getEncoded();
    }


    public StringKeyPair convertToStringKeyPair(KeyPair keyPair, String secret) {
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
            throw new IllegalStateException(String.format("converting KeyPair to StringKeyPair failed with msg: ['%s'].", e.getMessage()), e);
        }
    }

    public StringKeyPair generateStringKeyPair(final String secret) {
        try {
            return convertToStringKeyPair(generateKeyPair(), secret);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new IllegalStateException("could not generate key", e);
        }
    }

    public List unlock(final List list, final JwtAuthentication authentication) {
        StringKeyPair keyPair = stringKeyPairRepository.findByOwner(authentication.getEmail());
        if (Objects.isNull(keyPair)) {
            keyPair = stringKeyPairRepository.save(generateStringKeyPair(authentication.getId()));
        }
        list.setSecret(authentication.getId());
        list.setStringKeyPair(keyPair);
        return list;
    }
}
