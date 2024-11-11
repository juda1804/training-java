package org.booking.service;

import org.booking.data.repository.UserRepository;
import org.booking.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;

@Service
public class UserService {
    private final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final BiFunction<Integer, Integer, Pageable> createPageable = PageRequest::of;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Gets user by its id.
     * @return User.
     */
    public User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    LOGGER.error("User with id: {} cannot be found", userId);
                    return new NoSuchElementException(String.format("User with id: %s not found", userId));
                });
    }

    /**
     * Gets user by its email. Email is strictly matched.
     * @return User.
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .stream()
                .findFirst()
                .orElseThrow(() -> {
                    LOGGER.error("User with email: {} cannot be found", email);
                    return new NoSuchElementException(String.format("User with email: %s not found", email));
                });
    }

    /**
     * Get list of users by matching name. Name is matched using 'contains' approach.
     * In case nothing was found, empty list is returned.
     * @param name Users name or it's part.
     * @param pageSize Pagination param. Number of users to return on a page.
     * @param pageNum Pagination param. Number of the page to return. Starts from 1.
     * @return List of users.
     */
    public List<User> getUsersByName(String name, int pageSize, int pageNum) {
        Pageable pageable = createPageable.apply(pageNum, pageSize);
        return userRepository.findByNameContaining(name, pageable);
    }

    /**
     * Creates new user. User id should be auto-generated.
     * @param user User data.
     * @return Created User object.
     */
    public User create(User user) {
        LOGGER.info("Saving user with id: {}", user.getId());
        return userRepository.save(user);
    }

    /**
     * Updates user using given data.
     * @param user User data for update. Should have id set.
     * @return Updated User object.
     */
    public User update(User user) {
        LOGGER.info("Updating user with id: {}", user.getId());
        return userRepository.save(user);
    }

    /**
     * Deletes user by its id.
     * @param userId User id.
     * @return Flag that shows whether user has been deleted.
     */
    public boolean delete(long userId) {
        LOGGER.info("Deleting user with id: {}", userId);
        try {
            userRepository.deleteById(userId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean existsUser(long userId) {
        return userRepository.existsById(userId);
    }

}
