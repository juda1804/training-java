package org.booking.service;

import org.booking.data.repository.UserRepository;
import org.booking.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;

import static org.booking.util.IdentifierGenerator.generateId;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class UserServiceUnitTest {

    @Mock
    private UserRepository userDao;

    @InjectMocks
    private UserService userService;

    @Test
    public void testCreateUser() {
        var user = new User();
        user.setId(generateId());
        user.setName("John");
        user.setEmail("john@email.com");

        when(userDao.save(user)).thenReturn(user);

        var storedTicket = userService.create(user);

        Assertions.assertEquals(user.getId(), storedTicket.getId());
        Assertions.assertEquals(user.getName(), storedTicket.getName());
        Assertions.assertEquals(user.getEmail(), storedTicket.getEmail());
    }

    @Test
    public void testGetUserById() {
        var id = generateId();
        var user = new User();
        user.setId(id);
        user.setName("John");
        user.setEmail("john@email.com");

        when(userDao.findById(id)).thenReturn(Optional.of(user));

        var result = userService.getUserById(id);

        Assertions.assertEquals(id, result.getId());
    }

    @Test
    public void testFindUserByIdIfUserHasNotBeenStored() {
        var id = generateId();

        when(userDao.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(NoSuchElementException.class, () -> userService.getUserById(id));
    }

    @Test
    public void testGetUserByEmail() {
        var id = generateId();
        var user = new User();
        user.setId(id);
        user.setName("John");
        user.setEmail("john@email.com");

        when(userDao.findByEmail(user.getEmail())).thenReturn(List.of(user));

        var result = userService.getUserByEmail(user.getEmail());
        Assertions.assertEquals(id, result.getId());
    }

    @Test
    public void testGetUserByEmailIfUserHasNotBeenStored() {
        var id = generateId();
        var user = new User();
        user.setId(id);
        user.setName("John");
        user.setEmail("john@email.com");

        when(userDao.findByEmail(user.getEmail())).thenReturn(List.of());

        Assertions.assertThrows(NoSuchElementException.class, () -> userService.getUserByEmail(user.getEmail()));
    }

    @Test
    public void testGetUsersByName() {
        var name = "John";
        var email = "john@email.com";

        var user1 = new User();
        user1.setId(generateId());
        user1.setName(name);
        user1.setEmail(email);

        var user2 = new User();
        user2.setId(generateId());
        user2.setName(name);
        user2.setEmail(email);

        var user3 = new User();
        user3.setId(generateId());
        user3.setName(name);
        user3.setEmail(email);

        var size = 2;
        Function<Integer, Pageable> createPageable = (pageNum) -> PageRequest.of(pageNum, size);

        when(userDao.findByNameContaining(name, createPageable.apply(0)))
                .thenReturn(List.of(user1, user2));
        when(userDao.findByNameContaining(name, createPageable.apply(1)))
                .thenReturn(List.of(user3));
        when(userDao.findByNameContaining(name, createPageable.apply(2)))
                .thenReturn(List.of());

        var userPage1 = userService.getUsersByName(name, 2, 0);
        var userPage2 = userService.getUsersByName(name, 2, 1);
        var userPage3 = userService.getUsersByName(name, 2, 2);

        Assertions.assertEquals(2, userPage1.size());
        Assertions.assertEquals(1, userPage2.size());
        Assertions.assertEquals(0, userPage3.size());
    }

    @Test
    public void testUpdateUser() {
        var user = new User();
        user.setId(generateId());
        user.setName("John");
        user.setEmail("john@email.com");

        when(userDao.save(user)).thenReturn(user);

        var updatedTicket = userService.update(user);

        Assertions.assertEquals(user.getId(), updatedTicket.getId());
        Assertions.assertEquals(user.getName(), updatedTicket.getName());
        Assertions.assertEquals(user.getEmail(), updatedTicket.getEmail());
    }

    @Test
    public void testDeleteUser() {
        var id = generateId();

        var wasDeleted = userService.delete(id);
        Assertions.assertTrue(wasDeleted);
    }

    @Test
    public void testExistsUser() {
        var id = generateId();

        when(userDao.existsById(id)).thenReturn(true);

        var result = userService.existsUser(id);

        Assertions.assertTrue(result);
    }

    @Test
    public void testExistsUserIfUserHasNotBeenStored() {
        var id = generateId();

        when(userDao.existsById(id)).thenReturn(false);

        var result = userService.existsUser(id);

        Assertions.assertFalse(result);
    }
}
