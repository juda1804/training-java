package org.booking.restcontroller.auth;

import org.booking.auth.JwtUtil;
import org.booking.model.AccessToken;
import org.booking.model.UserInfo;
import org.booking.restcontroller.dto.BookingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthRestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthRestController.class);

    @Value("${spring.security.oauth2.client.provider.github.token-uri}")
    private String accessTokenUrl;

    @Value("${spring.security.oauth2.client.provider.github.user-info-uri}")
    private String userInfoUrl;

    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.github.redirect-uri}")
    private String redirectUri;

    private final RestTemplate restTemplate;

    public AuthRestController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/callback")
    public ResponseEntity<BookingResponse> callback(@RequestParam(name = "code") String code) {
        return getAccessToken(code)
                .flatMap(this::createAuthToken)
                .map(JwtUtil::createToken)
                .map(token -> ResponseEntity.ok(new BookingResponse(token)))
                .orElseGet(() -> ResponseEntity.badRequest().body(new BookingResponse("Auth token could not be created")));
    }

    private Optional<AccessToken> getAccessToken(String code) {
        try {
            var params = new LinkedMultiValueMap<String, String>();
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            params.add("redirect_uri", redirectUri);
            params.add("code", code);

            var headers = new HttpHeaders();
            headers.add("Accept", "application/json");

            var entity = new HttpEntity<>(params, headers);
            var response = restTemplate.exchange(accessTokenUrl, HttpMethod.POST, entity, AccessToken.class);

            return Optional.ofNullable(response.getBody());
        } catch (Exception ex) {
            LOGGER.error(String.format("Access token could not be created due to: %s", ex.getMessage()), ex);
            return Optional.empty();
        }
    }

    private Optional<UserInfo> createAuthToken(AccessToken accessToken) {
        try {
            var headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + accessToken.getAccessToken());
            headers.add("Accept", "application/json");

            var httpEntity = new HttpEntity<>(headers);
            var userInfo = restTemplate.exchange(userInfoUrl, HttpMethod.GET, httpEntity, UserInfo.class);

            return Optional.ofNullable(userInfo.getBody());
        } catch (Exception ex) {
            LOGGER.error(String.format("Auth token could not be created due to: %s", ex.getMessage()), ex);
            return Optional.empty();
        }
    }
}
