package de.my5t3ry.eternal.editor;

import de.my5t3ry.eternal.jwt.JwtAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Objects;

/**
 * User: my5t3ry
 * Date: 5/1/20 5:45 PM
 */
@Controller
@RequestMapping("editor")
public class EditorRestController {

    @Autowired
    private EditorStateReposiory editorStateReposiory;

    @PostMapping
    @Transactional
    public ResponseEntity<EditorState> save(@RequestBody String value, final JwtAuthentication authentication) {
        try {
            final EditorState existinState = editorStateReposiory.findByOwner(authentication.getEmail());
            if (Objects.nonNull(existinState)) {
                existinState.setValue(value);
                return new ResponseEntity(editorStateReposiory.save(existinState), HttpStatus.OK);

            } else {
                return new ResponseEntity(editorStateReposiory.save(new EditorState(authentication.getEmail(), value)), HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
    }

    @GetMapping(produces = "application/json")
    @Transactional
    public ResponseEntity<EditorState> get(final JwtAuthentication authentication) {
        try {
            final EditorState byOwner = editorStateReposiory.findByOwner(authentication.getEmail());
            if (Objects.isNull(byOwner)) {
                return new ResponseEntity<>(null, HttpStatus.OK);
            }
            return new ResponseEntity<>(byOwner, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.OK);
        }

    }
}
