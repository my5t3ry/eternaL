package de.my5t3ry.eternal.list;


import org.springframework.data.jpa.repository.JpaRepository;

/**
 * User: my5t3ry
 * Date: 5/15/20 4:00 AM
 */
public interface ListRepository extends JpaRepository<List, Long> {
    List findByOwner(String email);
}
