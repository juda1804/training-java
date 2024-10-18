package app.service;

import app.dao.UserRepository;
import app.domain.User;
import app.exceptions.UserAlreadyExist;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {
    private final UserRepository userDao;

    public UserService(UserRepository userDao) {
        this.userDao = userDao;
    }

    public User createUser(User user) {
        if (Objects.nonNull(getUserById(user.getId()))) {
            throw new UserAlreadyExist("User already exist");
        }

        log.info("Creating user with id {} and name {}", user.getId(), user.getName());
        userDao.save(user);
        return user;
    }

    public User getUserById(Long id) {
        log.info("Retrieving user with id {}", id);
        return userDao.findUserById(id);
    }

    public List<User> getAllUsers() {
        return (List<User>) userDao.findAll();
    }
}
