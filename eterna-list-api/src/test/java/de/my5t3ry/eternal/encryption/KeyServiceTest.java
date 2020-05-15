package de.my5t3ry.eternal.encryption;

import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * User: my5t3ry
 * Date: 5/15/20 7:22 AM
 */
class KeyServiceTest {

    @Test
    void generateStringKeyPair() {
        try {
            final StringKeyPair stringKeyPair = KeyService.convertToStringKeyPair(KeyService.generateKeyPair());
            System.out.print(stringKeyPair.getPublicKey());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
