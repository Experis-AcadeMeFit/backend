package me.fit.mefit.controllers;

import me.fit.mefit.models.Role;
import me.fit.mefit.models.RoleEnum;
import me.fit.mefit.models.User;
import me.fit.mefit.payload.request.SignupRequest;
import me.fit.mefit.payload.request.UserPatchRequest;
import me.fit.mefit.payload.request.PasswordChangeRequest;
import me.fit.mefit.payload.response.MessageResponse;
import me.fit.mefit.repositories.RoleRepository;
import me.fit.mefit.repositories.UserRepository;
import me.fit.mefit.security.jwt.JwtUtils;
import me.fit.mefit.utils.ApiPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.util.HashSet;
import java.util.NoSuchElementException;
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
    @PreAuthorize("hasRole('USER') or hasRole('CONTRIBUTOR') or hasRole('ADMIN')")
    @GetMapping()
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails principal = (UserDetails) authentication.getPrincipal();

        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();

        return ResponseEntity
                .status(HttpStatus.SEE_OTHER)
                .header("Location", ApiPaths.USER_PATH + "/" + user.getId())
                .build();
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
        Role role = roleRepository
                .findByRole(RoleEnum.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
        roles.add(role);

        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity
                .created(URI.create(ApiPaths.USER_PATH + "/" + user.getId()))
                .build();
    }

    /*
        Returns information pertaining to the referenced user.
    */
    @PreAuthorize("hasRole('USER') or hasRole('CONTRIBUTOR') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails principal = (UserDetails) authentication.getPrincipal();

        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        Role adminRole = roleRepository.findByRole(RoleEnum.ROLE_ADMIN).orElseThrow();

        if (user.getId() == id || user.getRoles().contains(adminRole)) {
            User returnUser = userRepository.findById(id).orElseThrow();
            return ResponseEntity.ok(returnUser);
        }

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .build();
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
    @PreAuthorize("hasRole('USER') or hasRole('CONTRIBUTOR') or hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable long id, @Valid @RequestBody UserPatchRequest userPatchRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails principal = (UserDetails) authentication.getPrincipal();

        User loggedInUser = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        Role adminRole = roleRepository.findByRole(RoleEnum.ROLE_ADMIN).orElseThrow();

        if (loggedInUser.getId() == id || loggedInUser.getRoles().contains(adminRole)) {
            User user = userRepository.findById(id).orElseThrow();

            if (userPatchRequest.getEmail() != null) {
                if (userRepository.existsByEmail(userPatchRequest.getEmail())) {
                    return ResponseEntity.badRequest().body("email already registered");
                }
                user.setEmail(userPatchRequest.getEmail());
            }

            if (userPatchRequest.getFirstName() != null) {
                user.setFirstName(userPatchRequest.getFirstName());
            }

            if (userPatchRequest.getLastName() != null) {
                user.setLastName(userPatchRequest.getLastName());
            }

            if (userPatchRequest.getRoles() != null) {
                // BACKDOOR FOR USER WITH ID = 1 !!
                if (loggedInUser.getRoles().contains(adminRole) || loggedInUser.getId() == 1) {
                    for (RoleEnum roleToAdd: userPatchRequest.getRoles()) {
                        Role roleObject = roleRepository.findByRole(roleToAdd).orElseThrow();
                        user.getRoles().add(roleObject);
                    }
                } else {
                    return ResponseEntity
                            .status(HttpStatus.FORBIDDEN)
                            .build();
                }
            }

            userRepository.save(user);

            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .build();
        }

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .build();
    }

    /*
        Deletes (cascading) a user. Self and admin only.
    */
    @PreAuthorize("hasRole('USER') or hasRole('CONTRIBUTOR') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails principal = (UserDetails) authentication.getPrincipal();

        User loggedInUser = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        Role adminRole = roleRepository.findByRole(RoleEnum.ROLE_ADMIN).orElseThrow();

        if (loggedInUser.getId() == id || loggedInUser.getRoles().contains(adminRole)) {
            User changeUser = userRepository.findById(id).orElseThrow();

            changeUser.setDeleted();
            userRepository.save(changeUser);

            return ResponseEntity
                    .noContent()
                    .build();
        }

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .build();
    }

    /*
    POST /user/:user_id/update_password
        Update a user’s password. Accepts appropriate parameters in the request body as
        application/json. Self only admin.

     */
    @PreAuthorize("hasRole('USER') or hasRole('CONTRIBUTOR') or hasRole('ADMIN')")
    @PostMapping("/{id}/update_password")
    public ResponseEntity<String> updatePassword(@PathVariable long id , @RequestBody PasswordChangeRequest passwordChangeRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails principal = (UserDetails) authentication.getPrincipal();

        User loggedInUser = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        Role adminRole = roleRepository.findByRole(RoleEnum.ROLE_ADMIN).orElseThrow();

        if (loggedInUser.getId() == id || loggedInUser.getRoles().contains(adminRole)) {
            User changeUser = userRepository.findById(id).orElseThrow();

            changeUser.setPassword(encoder.encode(passwordChangeRequest.getPassword()));
            userRepository.save(changeUser);

            return ResponseEntity
                    .noContent()
                    .build();
        }

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .build();
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus( HttpStatus.NOT_FOUND )
    public void NotFoundHandler(HttpServletRequest req, Exception ex) {
        logger.info("Invalid request received: " + req.getRequestURI());
        logger.info("Invalid request received: " + req.getMethod());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus( HttpStatus.BAD_REQUEST )
    public void InvalidArgumentHandler(HttpServletRequest req, Exception ex) {
        logger.info("Invalid request received: " + req.getRequestURI());
        logger.info("Invalid request received: " + req.getMethod());
    }
}
