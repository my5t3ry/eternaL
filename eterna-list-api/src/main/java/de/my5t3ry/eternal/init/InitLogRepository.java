package de.my5t3ry.eternal.init;

import org.springframework.data.repository.CrudRepository;

/**
 * Created by Jens Greive on 06.12.18.
 */
public interface InitLogRepository extends CrudRepository<InitLog, Integer> {

    InitLog findByMessage(final String message);

}
