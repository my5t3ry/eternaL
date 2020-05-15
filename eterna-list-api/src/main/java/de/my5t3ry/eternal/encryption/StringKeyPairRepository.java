package de.my5t3ry.eternal.encryption;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * User: my5t3ry
 * Date: 5/15/20 7:07 PM
 */
public interface StringKeyPairRepository extends JpaRepository<StringKeyPair, Long> {

    StringKeyPair findByOwner(String email);
}
