package de.my5t3ry.eternal.list;

import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Lob
    private String value;

    public List(final String owner, final String value, final ListType type) {
        this.owner = owner;
        this.value = value;
        this.listType = type;
    }
}
