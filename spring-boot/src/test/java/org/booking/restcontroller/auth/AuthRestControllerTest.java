package org.booking.restcontroller.auth;

import org.booking.TestWebApplication;
import org.booking.model.AccessToken;
import org.booking.model.UserInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.booking.util.IdentifierGenerator.generateId;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = TestWebApplication.class)
public class AuthRestControllerTest {

    private final RestTemplate restTemplate = new RestTemplate() {

        @Override
        public <T> ResponseEntity<T> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables) throws RestClientException {
            if (responseType == AccessToken.class) {
                var accessToken = new AccessToken();
                accessToken.setAccessToken("pto_12Psa1Tsq1Salga5OVXobCldlaoQpAt1n");
                accessToken.setTokenType("Bearer");
                accessToken.setScope("ADMIN");

                return new ResponseEntity(accessToken, HttpStatus.OK);
            } else if (responseType == UserInfo.class) {
                var userInfo = new UserInfo();
                userInfo.setId(Long.valueOf(generateId()).intValue());
                userInfo.setLogin("maria");
                userInfo.setEmail("maria@email.com");
                return new ResponseEntity(userInfo, HttpStatus.OK);
            } else {
                return new ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    };

    private final AuthRestController authRestController = new AuthRestController(restTemplate);

    @Test
    public void testCallbackUrl() {

        var response = authRestController.callback("3T6843b0235f663d8t912");

        var jwtToken = response.getBody().message();

        var periods = jwtToken.split("\\.");

        Assertions.assertEquals(3, periods.length);
    }
}
