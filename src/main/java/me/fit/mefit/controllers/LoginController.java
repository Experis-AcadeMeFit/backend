package me.fit.mefit.controllers;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import me.fit.mefit.payload.request.LoginRequest;
import me.fit.mefit.payload.response.JwtResponse;
import me.fit.mefit.repositories.RoleRepository;
import me.fit.mefit.repositories.UserRepository;
import me.fit.mefit.security.jwt.JwtUtils;
import me.fit.mefit.utils.ApiPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RequestMapping(ApiPaths.LOGIN_PATH)
@RestController
public class LoginController {


    private Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

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
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername(), roles));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus( HttpStatus.BAD_REQUEST )
    public void InvalidArgumentHandler(HttpServletRequest req, Exception ex) {
        logger.info("Invalid request received: " + req.getRequestURI());
        logger.info("Invalid request received: " + req.getMethod());
    }
}
