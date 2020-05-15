package de.my5t3ry.eternal.list;

import de.my5t3ry.eternal.encryption.KeyService;
import de.my5t3ry.eternal.encryption.StringKeyPair;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;

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

    public List(final String owner, final String value, final ListType type) {
        this.stringKeyPair = KeyService.generateStringKeyPair();
        this.owner = owner;
        setValue(value);
        this.listType = type;
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
        try {
            return KeyService.decrypt(stringKeyPair.getPrivateKey(), value);
        } catch (Exception e) {
            throw new IllegalStateException("could not dencode value", e);
        }
    }
}
