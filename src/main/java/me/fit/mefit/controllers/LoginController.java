package me.fit.mefit.controllers;

import me.fit.mefit.utils.ApiPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(ApiPaths.LOGIN_PATH)
@RestController
public class LoginController {
    Logger logger = LoggerFactory.getLogger(LoginController.class);

    /*
    @AutoWired
    private UserRepository userRepository;
    */

    /*
        Authenticates a user. Accepts appropriate parameters in the request body as application/json.

        A failed authentication attempt should prompt a 401 Unauthorized response. This
        endpoint should also be subject to a rate limiting policy where if authentication is
        attempted too many times (unsuccessfully) then requests from the corresponding address
        should be temporarily ignored. Candidates should decide on an appropriate threshold
        for rate limiting.

    */
    @PostMapping()
    public ResponseEntity<String> login(/* @RequestBody Type type */) {
        return ResponseEntity.ok("Not Implemented");
    }
}
