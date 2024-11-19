package org.booking.data.repository;

import org.booking.TestWebApplication;
import org.booking.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = TestWebApplication.class)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindByEmail() {
        // Crear y guardar un usuario
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        userRepository.save(user);

        // Consultar por email
        List<User> result = userRepository.findByEmail("john.doe@example.com");

        // Verificar que el usuario fue encontrado
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("John Doe", result.get(0).getName());
    }

    @Test
    public void testFindByNameContaining() {
        // Crear y guardar usuarios
        User user1 = new User();
        user1.setId(1L);
        user1.setName("Alice");
        user1.setEmail("alice@example.com");
        userRepository.save(user1);

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Alicia");
        user2.setEmail("alicia@example.com");
        userRepository.save(user2);

        User user3 = new User();
        user3.setId(3L);
        user3.setName("Bob");
        user3.setEmail("bob@example.com");
        userRepository.save(user3);

        // Consultar usuarios cuyo nombre contiene "Ali"
        List<User> result = userRepository.findByNameContaining("Ali", PageRequest.of(0, 10));

        // Verificar resultados
        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.stream().anyMatch(u -> u.getName().equals("Alice")));
        Assertions.assertTrue(result.stream().anyMatch(u -> u.getName().equals("Alicia")));
    }

    @Test
    public void testSaveAndRetrieveUser() {
        // Crear un usuario
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");

        // Guardar el usuario
        userRepository.save(user);

        // Recuperar el usuario
        User retrievedUser = userRepository.findById(1L).orElse(null);

        // Verificar que los datos coincidan
        Assertions.assertNotNull(retrievedUser);
        Assertions.assertEquals("John Doe", retrievedUser.getName());
        Assertions.assertEquals("john.doe@example.com", retrievedUser.getEmail());
    }
}