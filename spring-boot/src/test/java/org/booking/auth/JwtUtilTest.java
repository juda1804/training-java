package org.booking.auth;

import org.booking.model.UserInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.booking.util.IdentifierGenerator.generateId;

public class JwtUtilTest {

    @Test
    public void testCreateJwtToken() {
        var userInfo = new UserInfo();
        userInfo.setId(Long.valueOf(generateId()).intValue());
        userInfo.setLogin("maria");
        userInfo.setEmail("maria@email.com");

        var token = JwtUtil.createToken(userInfo);
        var periods = token.split("\\.");

        Assertions.assertEquals(3, periods.length);
    }

    @Test
    public void testGetJwtToken() {
        var userInfo = new UserInfo();
        userInfo.setId(Long.valueOf(generateId()).intValue());
        userInfo.setLogin("maria");
        userInfo.setEmail("maria@email.com");

        var token = JwtUtil.createToken(userInfo);

        var maybeClaims = JwtUtil.getJwtToken(String.format("Bearer %s", token));

        Assertions.assertTrue(maybeClaims.isPresent());
        var claims = maybeClaims.get();

        Assertions.assertEquals(userInfo.getLogin(), claims.getSubject());
    }

    @Test
    public void testGetJwtTokenWhenBearerTokenIsNotPresent() {
        var userInfo = new UserInfo();
        userInfo.setId(Long.valueOf(generateId()).intValue());
        userInfo.setLogin("maria");
        userInfo.setEmail("maria@email.com");

        var token = JwtUtil.createToken(userInfo);
        var maybeClaims = JwtUtil.getJwtToken(String.format("Basic %s", token));

        Assertions.assertTrue(maybeClaims.isEmpty());
    }
}
