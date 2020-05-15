package de.my5t3ry.eternal.encryption;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * User: my5t3ry
 * Date: 5/15/20 4:56 AM
 */
@NoArgsConstructor
@Entity
@Data
public class StringKeyPair {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String publicKey;
    private String privateKey;

    public StringKeyPair(final String publicKey, final String privateKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }
}
