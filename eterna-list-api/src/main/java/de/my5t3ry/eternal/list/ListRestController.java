package de.my5t3ry.eternal.list;

import de.my5t3ry.eternal.jwt.JwtAuthentication;
import de.my5t3ry.eternal.list.List;
import de.my5t3ry.eternal.list.ListRepository;
import de.my5t3ry.eternal.list.ListType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
@RequestMapping("list")
public class ListRestController {

    @Value("${list.default}")
    private String defaultListText;

    @Autowired
    private ListRepository listRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<List> save(@RequestBody String value, final JwtAuthentication authentication) {
        try {
            final List defaultList = listRepository.findByOwner(authentication.getEmail());
            defaultList.setSecret(authentication.getId());
            if (Objects.nonNull(defaultList)) {
                defaultList.setValue(value);
                return new ResponseEntity(listRepository.save(defaultList), HttpStatus.OK);
            } else {
                return new ResponseEntity(listRepository.save(createDefaultList(authentication)), HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
    }

    @GetMapping(path = "default", produces = "application/json")
    @Transactional
    public ResponseEntity<List> get(final JwtAuthentication authentication) {
        try {
            final List defaultList = listRepository.findByOwner(authentication.getEmail());
            if (Objects.isNull(defaultList)) {
                return new ResponseEntity<>(listRepository.save(createDefaultList(authentication)), HttpStatus.OK);
            }
            defaultList.setSecret(authentication.getId());
            return new ResponseEntity<>(defaultList, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(createDefaultList(authentication), HttpStatus.OK);
        }
    }

    private List createDefaultList(JwtAuthentication authentication) {
        return new List(authentication, defaultListText, ListType.DEFAULT);
    }
}
