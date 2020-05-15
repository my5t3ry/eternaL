package de.my5t3ry.eternal.list;

import de.my5t3ry.eternal.encryption.KeyService;
import de.my5t3ry.eternal.encryption.StringKeyPair;
import de.my5t3ry.eternal.jwt.JwtAuthentication;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.Objects;

/**
 * User: my5t3ry
 * Date: 5/15/20 3:51 AM
 */
@Entity
@NoArgsConstructor
@Data
public class List {
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
        this.stringKeyPair = KeyService.generateStringKeyPair(owner.getId());
        this.secret = owner.getId();
        this.owner = owner.getEmail();
        setValue(value);
        this.listType = type;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setValue(String value) {
        try {
            this.value = KeyService.encrypt(stringKeyPair.getPublicKey(), value);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("could not encode value", e);
        }
    }

    public String getValue() {
        if (Objects.isNull(secret)) {
            throw new IllegalStateException("secret must be set to decode value");
        }
        try {
            return KeyService.decrypt(stringKeyPair.getPrivateKey(), value, secret);
        } catch (Exception e) {
            throw new IllegalStateException("could not dencode value", e);
        }
    }
}
