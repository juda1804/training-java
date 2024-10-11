package app.service;

import app.dao.UserDao;
import app.domain.User;
import app.exceptions.UserAlreadyExist;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User createUser(User user) {
        if (Objects.nonNull(getUserById(user.getId()))) {
            throw new UserAlreadyExist("User already exist");
        }

        log.info("Creating user with id {} and name {}", user.getId(), user.getName());
        userDao.saveUser(user);
        return user;
    }

    public User getUserById(Long id) {
        log.info("Retrieving user with id {}", id);
        return userDao.findUserById(id);
    }
}
