package org.booking.data.repository;

import org.booking.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.booking.util.IdentifierGenerator.generateId;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/applicationContext.xml"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void resetDb() {
        userRepository.deleteAll();
    }

    @Test
    public void testSaveUser() {
        var user = new User();
        user.setId(generateId());
        user.setName("John");
        user.setEmail("john@email.com");

        var storedTicket = userRepository.save(user);
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

        var storedTicket1 = userRepository.save(user1);
        Assertions.assertEquals(user1.getId(), storedTicket1.getId());
        Assertions.assertEquals(user1.getName(), storedTicket1.getName());
        Assertions.assertEquals(user1.getEmail(), storedTicket1.getEmail());

        var user2 = new User();
        user2.setId(generateId());
        user2.setName("Maria");
        user2.setEmail("maria@email.com");

        var storedTicket2 = userRepository.save(user2);
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

        var storedTicket = userRepository.save(user);
        Assertions.assertEquals(user.getId(), storedTicket.getId());
        Assertions.assertEquals(user.getName(), storedTicket.getName());
        Assertions.assertEquals(user.getEmail(), storedTicket.getEmail());

        var result = userRepository.findById(id);
        Assertions.assertEquals(Optional.of(id), result.map(User::getId));
    }

    @Test
    public void testFindUserByIdIfUserHasNotBeenStored() {
        var id = generateId();

        var result = userRepository.findById(id);
        Assertions.assertEquals(Optional.empty(), result.map(User::getId));
    }

    @Test
    public void testExistsUser() {
        var id = generateId();
        var user = new User();
        user.setId(id);
        user.setName("John");
        user.setEmail("john@email.com");

        var storedTicket = userRepository.save(user);
        Assertions.assertEquals(user.getId(), storedTicket.getId());
        Assertions.assertEquals(user.getName(), storedTicket.getName());
        Assertions.assertEquals(user.getEmail(), storedTicket.getEmail());

        var result = userRepository.existsById(id);
        Assertions.assertTrue(result);
    }

    @Test
    public void testExistsUserIfUserHasNotBeenStored() {
        var id = generateId();

        var result = userRepository.existsById(id);
        Assertions.assertFalse(result);
    }

    @Test
    public void testFindUserByEmail() {
        var id = generateId();
        var user = new User();
        user.setId(id);
        user.setName("Teresa");
        user.setEmail("teresa@email.com");

        var storedTicket = userRepository.save(user);
        Assertions.assertEquals(user.getId(), storedTicket.getId());
        Assertions.assertEquals(user.getName(), storedTicket.getName());
        Assertions.assertEquals(user.getEmail(), storedTicket.getEmail());

        var result = userRepository.findByEmail(user.getEmail());
        Assertions.assertEquals(Optional.of(id), result.stream().map(User::getId).findFirst());
    }

    @Test
    public void testFilterUsersByName() {
        var name = "Peter";
        var email = "peter@email.com";

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
                .forEach(userRepository::save);

        var size = 2;
        Function<Integer, Pageable> createPage = (page) -> PageRequest.of(page, size);

        var userPage1 = userRepository.findByNameContaining(name, createPage.apply(0));
        var userPage2 = userRepository.findByNameContaining(name, createPage.apply(1));
        var userPage3 = userRepository.findByNameContaining(name, createPage.apply(2));

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

        var storedTicket = userRepository.save(user);
        Assertions.assertEquals(user.getId(), storedTicket.getId());
        Assertions.assertEquals(user.getName(), storedTicket.getName());
        Assertions.assertEquals(user.getEmail(), storedTicket.getEmail());

        user.setName("John Doe");

        var updatedTicket = userRepository.save(user);
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

        var updatedTicket = userRepository.save(user);
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

        var storedTicket = userRepository.save(user);
        Assertions.assertEquals(user.getId(), storedTicket.getId());
        Assertions.assertEquals(user.getName(), storedTicket.getName());
        Assertions.assertEquals(user.getEmail(), storedTicket.getEmail());

        var existsUserBeforeDeleting = userRepository.existsById(user.getId());
        Assertions.assertTrue(existsUserBeforeDeleting);

        userRepository.deleteById(user.getId());

        var existsUserAfterDeleting = userRepository.existsById(user.getId());
        Assertions.assertFalse(existsUserAfterDeleting);
    }
}
