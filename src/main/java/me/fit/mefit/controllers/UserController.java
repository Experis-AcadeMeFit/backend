package me.fit.mefit.controllers;

import me.fit.mefit.models.Role;
import me.fit.mefit.models.RoleEnum;
import me.fit.mefit.models.User;
import me.fit.mefit.payload.request.SignupRequest;
import me.fit.mefit.payload.response.MessageResponse;
import me.fit.mefit.repositories.RoleRepository;
import me.fit.mefit.repositories.UserRepository;
import me.fit.mefit.security.jwt.JwtUtils;
import me.fit.mefit.utils.ApiPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@RequestMapping(ApiPaths.USER_PATH)
@RestController
public class UserController {
    Logger logger = LoggerFactory.getLogger(UserController.class);

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
        Returns 303 See Other with the location header set to the URL of the currently
        authenticated user’s profile; for example:
        HTTP/1.1 303 See Other
        Location: https://example.com/user/:own_user_id
        This endpoint is effectively a shortcut to allow the currently authenticated user to be
        fetched without the client being aware of the user_id beforehand.
    */

    @GetMapping()
    public ResponseEntity<String> getCurrentUser() {
        return ResponseEntity.ok("Not Implemented");
    }

    /*
        Register a new user. Accepts appropriate parameters in the request body as application/json.
    */

    @PostMapping()
    public ResponseEntity<?> createUser(@Valid @RequestBody SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken"));
        }

        User user = new User(
                encoder.encode(signupRequest.getPassword()),
                signupRequest.getFirstname(),
                signupRequest.getLastname(),
                signupRequest.getEmail()
        );

        Set<Role> roles = new HashSet<>();
        Role role = roleRepository.findByRole(RoleEnum.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role is not found"));
        roles.add(role);

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully"));
    }

    /*
        Returns information pertaining to the referenced user.
    */

    @GetMapping("/{id}")
    public ResponseEntity<String> getUser(@PathVariable long id) {
        return ResponseEntity.ok("Not Implmented");
    }

    /*
        Makes a partial update to the user object with the options that have been supplied.
        Accepts appropriate parameters in the request body as application/json.
        If an attempt is made to update the password or password hash then the server should
        return 400 Bad Request. Updating users’ passwords should instead be done using the
        update_password endpoint described below.
        Only administrators may modify users’ isAdmin property. All unauthorized attempts
        to do so should make no changes to the user’s record and respond with 403 Forbidden.
    */

    @PatchMapping("/{id}")
    public ResponseEntity<String> updateUser(@PathVariable long id /*, @RequestBody Type type */) {
        return ResponseEntity.ok("Not Implemented");
    }

    /*
        Deletes (cascading) a user. Self and admin only.
    */

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable long id) {
        return ResponseEntity.ok("Not Implemented");
    }
    /*
    POST /user/:user_id/update_password
        Update a user’s password. Accepts appropriate parameters in the request body as
        application/json. Self only admin.

     */
    @PostMapping("/{id}/update_password")
    public ResponseEntity<String> updatePassword(@PathVariable long id /*, @RequestBody Type type */) {
        return ResponseEntity.ok("Not Implemented");
    }
}
