package org.booking.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.booking.model.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

import static org.booking.util.DateConverter.convertToDate;

public class JwtUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtil.class);

    private static final String secret = "A!5@60lAq_aaPstlq@12";
    private static final int expirationDurationMin = 60;
    private static final String bearerPrefix = "Bearer ";

    public static String createToken(UserInfo userInfo) {
        LOGGER.debug("login {}", userInfo.getLogin());
        var now = LocalDateTime.now(Clock.systemUTC());

        var claims = new HashMap<String, Object>();
        claims.put("id", userInfo.getId());
        claims.put("username", userInfo.getLogin());
        claims.put("email", userInfo.getEmail());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userInfo.getLogin())
                .setIssuedAt(convertToDate(now))
                .setExpiration(convertToDate(now.plusMinutes(expirationDurationMin)))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public static Optional<Claims> getJwtToken(String authorizationHeader) {
        LOGGER.debug("Authorization: {}", authorizationHeader);

        return Optional.ofNullable(authorizationHeader)
                .filter(token -> token.startsWith(bearerPrefix))
                .map(bearerToken -> bearerToken.substring(bearerPrefix.length()))
                .flatMap(JwtUtil::extractJwtToken);
    }

    private static Optional<Claims> extractJwtToken(String token) {
        try {
            return Optional.of(Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody());
        } catch (Exception ex) {
            LOGGER.error(String.format("JWT token cannot be parser due to: %s", ex.getMessage()), ex);
            return Optional.empty();
        }
    }

}
