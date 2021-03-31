package me.fit.mefit.keysecurity.services;

import me.fit.mefit.payload.request.LoginRequest;
import me.fit.mefit.payload.response.JwtResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class LoginService {
    private static final String LOGIN_PATH = "/auth/realms/%s/protocol/openid-connect/token";

    private Logger logger = LoggerFactory.getLogger(LoginService.class);

    @Value("${mefit.app.loginClientId}") String clientId;
    @Value("${mefit.app.keycloakAddress}") String address;
    @Value("${mefit.app.keycloakRealm}") String realm;

    public JwtResponse performLogin(LoginRequest loginRequest) {
        logger.info("Logging client in");

        WebClient client = WebClient.builder()
                .baseUrl(address)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();

        return client.post()
                .uri( String.format(LOGIN_PATH, realm))
                .body( BodyInserters
                        .fromFormData("username", loginRequest.getEmail())
                        .with("password", loginRequest.getPassword())
                        .with("grant_type", "password")
                        .with("client_id", clientId))
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> {
                    if (clientResponse.statusCode() == HttpStatus.UNAUTHORIZED) {
                        throw new BadCredentialsException("Keycloak says wrong username or password");
                    }
                    throw new RuntimeException();
                })
                .bodyToMono(JwtResponse.class)
                .block();
    }
}
