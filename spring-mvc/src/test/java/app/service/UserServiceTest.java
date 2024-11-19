package app.service;

import app.dao.UserAccountRepository;
import app.dao.UserRepository;
import app.domain.User;
import app.domain.UserAccount;
import app.exceptions.UserAlreadyExist;
import com.github.javafaker.Faker;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {
    private Faker faker =  Faker.instance();

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserAccountRepository userAccountRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser_UserAlreadyExists() {
        // Arrange
        User user = createAnUser();

        user.setId(1L);
        when(userRepository.findUserById(user.getId())).thenReturn(Optional.of(user));
        // Act & Assert
        assertThrows(UserAlreadyExist.class, () -> userService.createUser(user));

        // Verify
        verify(userRepository, times(1)).findUserById(user.getId());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testCreateUser_UserDoesNotExist() {
        // Arrange
        User user = createAnUser();
        when(userRepository.findUserById(user.getId())).thenReturn(Optional.empty());

        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userAccountRepository.save(any(UserAccount.class))).thenReturn(new UserAccount());

        // Act
        User createdUser = userService.createUser(user);

        // Assert
        assertNotNull(createdUser);
        assertEquals(user.getId(), createdUser.getId());
        assertEquals(user.getName(), createdUser.getName());

        // Verify
        verify(userRepository, times(0)).findUserById(user.getId());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testGetUserById() {
        // Arrange
        Long userId = 1L;
        User user = createAnUser();
        user.setId(userId);
        when(userRepository.findUserById(userId)).thenReturn(Optional.of(user));

        // Act
        User foundUser = userService.getUserById(userId);

        // Assert
        assertNotNull(foundUser);
        assertEquals(userId, foundUser.getId());
        assertEquals(user.getName(), foundUser.getName());

        // Verify
        verify(userRepository, times(1)).findUserById(userId);
    }

    private User createAnUser() {
        User user = new User();
        String name = faker.name().firstName();
        user.setName(name);
        user.setEmail(name + "@email.com");
        return user;
    }
}