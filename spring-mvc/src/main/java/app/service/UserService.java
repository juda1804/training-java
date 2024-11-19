package app.service;

import app.dao.UserAccountRepository;
import app.dao.UserRepository;
import app.domain.User;
import app.domain.UserAccount;
import app.exceptions.UserAlreadyExist;
import app.exceptions.UserNotFound;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserAccountRepository userAccountRepository;

    public UserService(UserRepository userRepository, UserAccountRepository userAccountRepository) {
        this.userRepository = userRepository;
        this.userAccountRepository = userAccountRepository;
    }

    public User createUser(User createUser) {
        if(Objects.nonNull(createUser.getId())) {
            User user = getUserById(createUser.getId());
            if (Objects.nonNull(user)) {
                throw new UserAlreadyExist("User already exist");
            }
        }

        log.debug("Creating user with id {} and name {}", createUser.getId(), createUser.getName());
        User user = userRepository.save(createUser);
        userAccountRepository.save(new UserAccount(user, 0));
        return user;
    }

    public UserAccount saveUserAccount(UserAccount userAccount) {
        return userAccountRepository.save(userAccount);
    }

    public User getUserById(Long id) {
        log.debug("Retrieving user with id {}", id);
        return userRepository.findUserById(id).orElseThrow(UserNotFound::new);
    }

    public UserAccount getUserAccountByUserId(Long id) {
        log.debug("Retrieving user account with id {}", id);

        return userAccountRepository.findByUserId(id)
                .orElseThrow(UserNotFound::new);
    }

    public List<User> getAllUsers() {
        return (List<User>) userRepository.findAll();
    }
}
