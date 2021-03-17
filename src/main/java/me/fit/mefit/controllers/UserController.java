package me.fit.mefit.controllers;

import me.fit.mefit.utils.ApiPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(ApiPaths.USER_PATH)
@RestController
public class UserController {
    Logger logger = LoggerFactory.getLogger(UserController.class);

    /*
    @AutoWired
    private UserRepository userRepository;
    */

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
    public ResponseEntity<String> createUser( /*@RequestBody Type type */) {
        return ResponseEntity.ok("Not Implemented");
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
