package de.my5t3ry.eternal.encryption;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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

    @Lob
    private String publicKey;

    @Lob
    private String privateKey;

    private String owner;

    public StringKeyPair(final String publicKey, final String privateKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

}
