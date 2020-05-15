package de.my5t3ry.eternal.list;

import de.my5t3ry.eternal.encryption.StringKeyPair;
import de.my5t3ry.eternal.jwt.JwtAuthentication;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;

import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.persistence.*;
import java.security.*;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;

import static de.my5t3ry.eternal.encryption.EncryptionUtils.*;

/**
 * User: my5t3ry
 * Date: 5/15/20 3:51 AM
 */
@Entity
@NoArgsConstructor
@Data
public class List {


    private final String DEFAULT_KEY_ALGORITHM = "RSA";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private ListType listType;
    private String owner;
    @OneToOne
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private StringKeyPair stringKeyPair;
    @Lob
    private String value;
    @Transient
    private String secret;

    public List(final JwtAuthentication owner, final String value, final ListType type) {
        this.secret = owner.getId();
        this.owner = owner.getEmail();
        this.listType = type;
    }


    private byte[] encrypt(byte[] publicKey, byte[] inputData) throws Exception {
        PublicKey key = KeyFactory.getInstance(DEFAULT_KEY_ALGORITHM)
                .generatePublic(new X509EncodedKeySpec(publicKey));
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.PUBLIC_KEY, key);
        return cipher.doFinal(inputData);
    }

    public String encrypt(String publicKey, String inputData) throws Exception {
        return bytesToEncodedString(encrypt(encodedStringToBytes(publicKey), stringToBytes(inputData)));
    }

    private byte[] decrypt(byte[] privateKey, byte[] inputData) throws Exception {
        PrivateKey key = KeyFactory.getInstance(DEFAULT_KEY_ALGORITHM)
                .generatePrivate(new PKCS8EncodedKeySpec(privateKey));
        Cipher cipher = Cipher.getInstance(DEFAULT_KEY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(inputData);
    }

    public String decrypt() throws Exception {
        return bytesToString(decrypt(
                decodePrivateKey(encodedStringToBytes(stringKeyPair.getPrivateKey()), secret),
                encodedStringToBytes(value)));
    }


    private byte[] decodePrivateKey(byte[] privateKey, String secret) {
        try {
            EncryptedPrivateKeyInfo encryptPKInfo = new EncryptedPrivateKeyInfo(privateKey);
            Cipher cipher = Cipher.getInstance(encryptPKInfo.getAlgName());
            PBEKeySpec pbeKeySpec = new PBEKeySpec(secret.toCharArray());
            SecretKeyFactory secFac = SecretKeyFactory.getInstance(encryptPKInfo.getAlgName());
            Key pbeKey = secFac.generateSecret(pbeKeySpec);
            AlgorithmParameters algParams = encryptPKInfo.getAlgParameters();
            cipher.init(Cipher.DECRYPT_MODE, pbeKey, algParams);
            KeySpec pkcs8KeySpec = encryptPKInfo.getKeySpec(cipher);
            KeyFactory kf = KeyFactory.getInstance(DEFAULT_KEY_ALGORITHM);
            return kf.generatePrivate(pkcs8KeySpec).getEncoded();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setValue(String value) {
        isUnlocked();
        try {
            this.value = encrypt(stringKeyPair.getPublicKey(), value);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("could not encode value", e);
        }
    }

    public String getValue() {
        isUnlocked();
        try {
            return decrypt();
        } catch (Exception e) {
            throw new IllegalStateException("could not dencode value", e);
        }
    }

    private void isUnlocked() {
        if (Objects.isNull(secret) || Objects.isNull(stringKeyPair)) {
            throw new IllegalStateException("list is locked");
        }
    }
}
