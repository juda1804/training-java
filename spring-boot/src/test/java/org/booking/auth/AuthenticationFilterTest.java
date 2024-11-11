package org.booking.auth;

import org.booking.TestWebApplication;
import org.booking.model.UserInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.booking.util.IdentifierGenerator.generateId;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestWebApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationFilterTest {
    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup( context )
                .addFilter(new AuthenticationFilter(), "/*")
                .build();
    }

    @Test
    public void testFilter() throws Exception {
        var userInfo = new UserInfo();
        userInfo.setId(Long.valueOf(generateId()).intValue());
        userInfo.setLogin("maria");
        userInfo.setEmail("maria@email.com");

        var token = JwtUtil.createToken(userInfo);

        this.mockMvc.perform(get("/users/email/maria@email.com")
                        .header("Authorization", String.format("Bearer %s", token)))
                .andExpect( status().is2xxSuccessful() );
    }
}
