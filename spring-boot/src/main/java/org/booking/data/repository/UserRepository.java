package org.booking.data.repository;

import org.booking.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Gets user by its email. Email is strictly matched.
     * @return User.
     */
    List<User> findByEmail(String email);

    /**
     * Get list of users by matching name. Name is matched using 'contains' approach.
     * In case nothing was found, empty list is returned.
     * @param name Users name or it's part.
     * @param pageable Pagination param.
     * @return List of users.
     */
    List<User> findByNameContaining(String name, Pageable pageable);
}
