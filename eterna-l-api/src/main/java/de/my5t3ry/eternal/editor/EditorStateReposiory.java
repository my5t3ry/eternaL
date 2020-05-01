package de.my5t3ry.eternal.editor;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * User: my5t3ry
 * Date: 5/1/20 5:44 PM
 */
public interface EditorStateReposiory extends JpaRepository<EditorState, Long> {
    EditorState findByOwner(String email);
}
