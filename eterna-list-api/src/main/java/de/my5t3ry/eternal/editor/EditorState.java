package de.my5t3ry.eternal.editor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * User: my5t3ry
 * Date: 5/1/20 5:30 PM
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class EditorState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String owner;

    @Lob
    private String value;

    public EditorState(final String owner, final String value) {
        this.owner = owner;
        this.value = value;
    }
}
