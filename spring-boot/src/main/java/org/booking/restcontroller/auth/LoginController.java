package org.booking.restcontroller.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/auth")
public class LoginController {

    @Value("${spring.security.oauth2.client.provider.github.authorization-uri}")
    private String authorizationUrl;

    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.github.redirect-uri}")
    private String redirectUri;

    @Value("${spring.security.oauth2.client.registration.github.scope}")
    private String scope;

    @GetMapping("/login")
    public RedirectView login() {
        var url = String.format("%s?client_id=%s&redirect_uri=%s&scope=%s", authorizationUrl, clientId, redirectUri, scope);
        return new RedirectView(url);
    }
}
