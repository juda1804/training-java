package app.service;

import app.dao.UserDao;
import app.domain.User;
import app.exceptions.UserAlreadyExist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser_UserAlreadyExists() {
        // Arrange
        User user = new User(1L, "John Doe", "email@email.com");
        when(userDao.findUserById(user.getId())).thenReturn(user);

        // Act & Assert
        assertThrows(UserAlreadyExist.class, () -> userService.createUser(user));

        // Verify
        verify(userDao, times(1)).findUserById(user.getId());
        verify(userDao, never()).saveUser(any(User.class));
    }

    @Test
    void testCreateUser_UserDoesNotExist() {
        // Arrange
        User user = new User(1L, "John Doe", "email@email.com");
        when(userDao.findUserById(user.getId())).thenReturn(null);

        // Act
        User createdUser = userService.createUser(user);

        // Assert
        assertNotNull(createdUser);
        assertEquals(user.getId(), createdUser.getId());
        assertEquals(user.getName(), createdUser.getName());

        // Verify
        verify(userDao, times(1)).findUserById(user.getId());
        verify(userDao, times(1)).saveUser(user);
    }

    @Test
    void testGetUserById() {
        // Arrange
        Long userId = 1L;
        User user = new User(userId, "John Doe", "email@email.com");
        when(userDao.findUserById(userId)).thenReturn(user);

        // Act
        User foundUser = userService.getUserById(userId);

        // Assert
        assertNotNull(foundUser);
        assertEquals(userId, foundUser.getId());
        assertEquals("John Doe", foundUser.getName());

        // Verify
        verify(userDao, times(1)).findUserById(userId);
    }
}