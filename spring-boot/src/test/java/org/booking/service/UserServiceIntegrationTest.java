package org.booking.service;

import org.booking.TestWebApplication;
import org.booking.data.repository.UserRepository;
import org.booking.model.User;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.NoSuchElementException;
import static org.booking.util.IdentifierGenerator.generateId;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = TestWebApplication.class)
public class UserServiceIntegrationTest {

    @Autowired
    private UserRepository userDao;
    @Autowired
    private UserService userService;

    @Before
    public void resetDb() {
        userDao.deleteAll();
    }

    @Test
    public void testSaveUser() {
        var user = new User();
        user.setId(generateId());
        user.setName("John");
        user.setEmail("john@email.com");

        var storedTicket = userService.create(user);
        Assertions.assertEquals(user.getId(), storedTicket.getId());
        Assertions.assertEquals(user.getName(), storedTicket.getName());
        Assertions.assertEquals(user.getEmail(), storedTicket.getEmail());
    }

    @Test
    public void testSaveUserIfItWasAlreadyStored() {
        var user1 = new User();
        user1.setId(generateId());
        user1.setName("John");
        user1.setEmail("john@email.com");

        var storedTicket1 = userService.create(user1);
        Assertions.assertEquals(user1.getId(), storedTicket1.getId());
        Assertions.assertEquals(user1.getName(), storedTicket1.getName());
        Assertions.assertEquals(user1.getEmail(), storedTicket1.getEmail());

        var user2 = new User();
        user2.setId(generateId());
        user2.setName("Maria");
        user2.setEmail("maria@email.com");

        var storedTicket2 = userService.create(user2);
        Assertions.assertEquals(user2.getId(), storedTicket2.getId());
        Assertions.assertEquals(user2.getName(), storedTicket2.getName());
        Assertions.assertEquals(user2.getEmail(), storedTicket2.getEmail());
    }

    @Test
    public void testFindUserById() {
        var id = generateId();
        var user = new User();
        user.setId(id);
        user.setName("John");
        user.setEmail("john@email.com");

        var storedTicket = userDao.save(user);
        Assertions.assertEquals(user.getId(), storedTicket.getId());
        Assertions.assertEquals(user.getName(), storedTicket.getName());
        Assertions.assertEquals(user.getEmail(), storedTicket.getEmail());

        var result = userService.getUserById(id);
        Assertions.assertEquals(id, result.getId());
    }

    @Test
    public void testFindUserByIdIfUserHasNotBeenStored() {
        var id = generateId();

        Assertions.assertThrows(NoSuchElementException.class, () -> userService.getUserById(id));
    }

    @Test
    public void testFindUserByEmail() {
        var id = generateId();
        var user = new User();
        user.setId(id);
        user.setName("John");
        user.setEmail("john@email.com");

        var storedTicket = userDao.save(user);
        Assertions.assertEquals(user.getId(), storedTicket.getId());
        Assertions.assertEquals(user.getName(), storedTicket.getName());
        Assertions.assertEquals(user.getEmail(), storedTicket.getEmail());

        var result = userService.getUserByEmail(user.getEmail());
        Assertions.assertEquals(id, result.getId());
    }

    @Test
    public void testFilterUsersByName() {
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
        user3.setName("Maria");
        user3.setEmail("maria@email.com");

        var user4 = new User();
        user4.setId(generateId());
        user4.setName(name);
        user4.setEmail(email);

        List.of(user1, user2, user3,user4)
                .forEach(userDao::save);

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

        var storedTicket = userDao.save(user);
        Assertions.assertEquals(user.getId(), storedTicket.getId());
        Assertions.assertEquals(user.getName(), storedTicket.getName());
        Assertions.assertEquals(user.getEmail(), storedTicket.getEmail());

        user.setName("John Doe");

        var updatedTicket = userService.update(user);
        Assertions.assertEquals(user.getId(), updatedTicket.getId());
        Assertions.assertEquals(user.getName(), updatedTicket.getName());
        Assertions.assertEquals(user.getEmail(), updatedTicket.getEmail());
    }

    @Test
    public void testUpdateUserIfItHasNotBeenStored() {
        var user = new User();
        user.setId(generateId());
        user.setName("John");
        user.setEmail("john@email.com");

        var updatedTicket = userService.update(user);
        Assertions.assertEquals(user.getId(), updatedTicket.getId());
        Assertions.assertEquals(user.getName(), updatedTicket.getName());
        Assertions.assertEquals(user.getEmail(), updatedTicket.getEmail());
    }

    @Test
    public void testDeleteUser() {
        var user = new User();
        user.setId(generateId());
        user.setName("John");
        user.setEmail("john@email.com");

        var storedTicket = userDao.save(user);
        Assertions.assertEquals(user.getId(), storedTicket.getId());
        Assertions.assertEquals(user.getName(), storedTicket.getName());
        Assertions.assertEquals(user.getEmail(), storedTicket.getEmail());

        var wasDeleted = userService.delete(user.getId());
        Assertions.assertTrue(wasDeleted);
    }

    @Test
    public void testExistUser() {
        var id = generateId();
        var user = new User();
        user.setId(id);
        user.setName("John");
        user.setEmail("john@email.com");

        var storedTicket = userDao.save(user);
        Assertions.assertEquals(user.getId(), storedTicket.getId());
        Assertions.assertEquals(user.getName(), storedTicket.getName());
        Assertions.assertEquals(user.getEmail(), storedTicket.getEmail());

        var result = userService.existsUser(id);
        Assertions.assertTrue(result);
    }

    @Test
    public void testExistUserIfUserHasNotBeenStored() {
        var id = generateId();

        var result = userService.existsUser(id);
        Assertions.assertFalse(result);
    }

}
