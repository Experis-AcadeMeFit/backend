package me.fit.mefit.controllers;

import me.fit.mefit.utils.ApiPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(ApiPaths.PROFILE_PATH)
@RestController
public class ProfileController {
    Logger logger = LoggerFactory.getLogger(ProfileController.class);

    /*
    @AutoWired
    private ProfileRepository profileRepository;
    */

    /*
        Returns detail about current state of the users profile.
    */
    @GetMapping("/{id}")
    public ResponseEntity<String> getProfile(@PathVariable long id) {
        return ResponseEntity.ok("Not Implemented");
    }

    /*
        Creates a new profile. Accepts appropriate parameters in the profile body as application/json.
    */
    @PostMapping()
    public ResponseEntity<String> createProfile(/* @RequestBody Type type */) {
        return ResponseEntity.ok("Not Implemented");
    }

    /*
        Executes a partial update of the corresponding profile_id. Accepts appropriate
        parameters in the request body as application/json.
    */
    @PatchMapping("/{id}")
    public ResponseEntity<String> updateProfile(@PathVariable long id /*, @RequestBody Type type */) {
        return ResponseEntity.ok("Not Implemented");
    }

    /*
        Deletes a profile. User only.
    */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProfile(@PathVariable long id) {
        return ResponseEntity.ok("Not Implemented");
    }

}
