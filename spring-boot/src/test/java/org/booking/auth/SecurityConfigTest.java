package org.booking.auth;

import org.booking.TestWebApplication;
import org.booking.data.repository.UserRepository;
import org.booking.model.User;
import org.booking.model.UserInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import static org.booking.util.IdentifierGenerator.generateId;

@SpringBootTest(classes = TestWebApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SecurityConfigTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void testSecuredEndpointWithoutAuthentication() {
        var user = new User();
        user.setName("Joe");
        user.setEmail("joe@email.com");

        var response = testRestTemplate.postForEntity("/users/new", user, String.class);

        Assertions.assertTrue(response.getStatusCode().is3xxRedirection());
    }

    @Test
    public void testSecuredEndpointWithAuthentication() {
        var token = createToken();
        var user = new User();
        user.setName("Joe");
        user.setEmail("joe@email.com");

        var headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);

        var httpEntity = new HttpEntity<>(user, headers);

        var response = testRestTemplate.postForEntity("/users/new", httpEntity, User.class);

        var maybeUser = userRepository.findById(response.getBody().getId());

        Assertions.assertTrue(maybeUser.isPresent());
    }

    private String createToken() {
        var userInfo = new UserInfo();
        userInfo.setId(Long.valueOf(generateId()).intValue());
        userInfo.setLogin("maria");
        userInfo.setEmail("maria@email.com");

        return JwtUtil.createToken(userInfo);
    }

}
