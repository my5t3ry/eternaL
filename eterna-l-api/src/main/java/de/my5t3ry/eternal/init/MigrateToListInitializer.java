package de.my5t3ry.eternal.init;

import de.my5t3ry.eternal.editor.EditorStateRepository;
import de.my5t3ry.eternal.list.List;
import de.my5t3ry.eternal.list.ListRepository;
import de.my5t3ry.eternal.list.ListType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Objects;

/**
 * Created by Jens Greive on 06.12.18.
 */
@Component
@Slf4j
public class MigrateToListInitializer {

    @Autowired
    private InitLogRepository initLogRepository;

    @Autowired
    private EditorStateRepository editorStateRepository;

    @Autowired
    private ListRepository listRepository;

    @PostConstruct
    public void init() {
        String initLog = "MIGRATE_LISTS";
        if (Objects.isNull(initLogRepository.findByMessage(initLog))) {
            editorStateRepository.findAll().forEach(curState -> {
                listRepository.save(new List(curState.getOwner(), curState.getValue(), ListType.DEFAULT));
            });
            initLogRepository.save(new InitLog(initLog, initLog));
        }
    }
}
