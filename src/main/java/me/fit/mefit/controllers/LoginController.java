package me.fit.mefit.controllers;

import me.fit.mefit.keysecurity.services.LoginService;
import me.fit.mefit.payload.request.LoginRequest;
import me.fit.mefit.payload.response.JwtResponse;
import me.fit.mefit.repositories.RoleRepository;
import me.fit.mefit.repositories.UserRepository;
import me.fit.mefit.security.jwt.JwtUtils;
import me.fit.mefit.utils.ApiPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(ApiPaths.LOGIN_PATH)
@RestController
public class LoginController {
    private Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired UserRepository userRepository;
    @Autowired RoleRepository roleRepository;
    @Autowired LoginService loginService;

    // These three are only used when we use local auth
    @Autowired @Lazy AuthenticationManager authenticationManager;
    @Autowired @Lazy PasswordEncoder encoder;
    @Autowired @Lazy JwtUtils jwtUtils;

    @Value("${mefit.app.usingKeycloak}") boolean usingKeycloak;

       /*
        Authenticates a user. Accepts appropriate parameters in the request body as application/json.

        A failed authentication attempt should prompt a 401 Unauthorized response. This
        endpoint should also be subject to a rate limiting policy where if authentication is
        attempted too many times (unsuccessfully) then requests from the corresponding address
        should be temporarily ignored. Candidates should decide on an appropriate threshold
        for rate limiting.

    */
    @PostMapping()
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        if (usingKeycloak) {
            return ResponseEntity.ok( loginService.performLogin(loginRequest) );
        } else {
            Authentication auth = authenticationManager
            .authenticate( new UsernamePasswordAuthenticationToken( loginRequest.getEmail(), loginRequest.getPassword() ) );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(auth);

            String jwt = jwtUtils.generateJwtToken(auth);

            return ResponseEntity.ok( new JwtResponse(jwt, "Bearer") );
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus( HttpStatus.BAD_REQUEST )
    public void invalidArgumentHandler(HttpServletRequest req, Exception ex) {
        logger.info("Invalid request received: {}", req.getRequestURI());
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus( HttpStatus.UNAUTHORIZED )
    public void unauthorizedHandler(HttpServletRequest req, Exception ex) {
        logger.info("Unauthorized request received: {}", req.getRequestURI());
    }

    @ExceptionHandler(WebClientRequestException.class)
    @ResponseStatus( HttpStatus.INTERNAL_SERVER_ERROR )
    public void webClientExceptionHandler(HttpServletRequest req, WebClientRequestException ex) {
        logger.info("WebClient threw exception: {}", ex.getLocalizedMessage());
    }


}
