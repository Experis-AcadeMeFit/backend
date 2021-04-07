package me.fit.mefit.controllers;

import me.fit.mefit.keysecurity.services.AuthAdapter;
import me.fit.mefit.models.*;
import me.fit.mefit.payload.request.ProfileCreateRequest;
import me.fit.mefit.payload.request.ProfilePatchRequest;
import me.fit.mefit.repositories.ProfileRepository;
import me.fit.mefit.repositories.RoleRepository;
import me.fit.mefit.repositories.UserRepository;
import me.fit.mefit.utils.ApiPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.util.NoSuchElementException;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(ApiPaths.PROFILE_PATH)
@RestController
public class ProfileController {
    Logger logger = LoggerFactory.getLogger(ProfileController.class);

    @Autowired AuthAdapter authAdapter;
    @Autowired ProfileRepository profileRepository;
    @Autowired UserRepository userRepository;
    @Autowired RoleRepository roleRepository;

    /*
        Return the profile of the current user. Not in specs. Move if we need "get all profiles"
    */

    @PreAuthorize("hasRole('USER') or hasRole('CONTRIBUTOR') or hasRole('ADMIN')")
    @GetMapping()
    public ResponseEntity<?> getCurrentProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = authAdapter.getUser(authentication.getPrincipal());

        return ResponseEntity
                .status(HttpStatus.SEE_OTHER)
                .location(URI.create(ApiPaths.PROFILE_PATH + "/" + user.getProfile().getId()))
                .build();
    }

    /*
        Returns detail about current state of the users profile.
    */
    @PreAuthorize("hasRole('USER') or hasRole('CONTRIBUTOR') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getProfile(@PathVariable long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User loggedInUser = authAdapter.getUser(authentication.getPrincipal());
        Role adminRole = roleRepository.findByRole(RoleEnum.ROLE_ADMIN).orElseThrow();

        if (id == loggedInUser.getProfile().getId() || loggedInUser.getRoles().contains(adminRole)) {
            Profile fetchProfile = profileRepository.findById(id).orElseThrow();
            return ResponseEntity.ok(fetchProfile);
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    /*
        Creates a new profile. Accepts appropriate parameters in the profile body as application/json.
    */
    @PreAuthorize("hasRole('USER') or hasRole('CONTRIBUTOR') or hasRole('ADMIN')")
    @PostMapping()
    public ResponseEntity<?> createProfile(@Valid @RequestBody ProfileCreateRequest profileCreateRequest ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User loggedInUser = authAdapter.getUser(authentication.getPrincipal());
        Role adminRole = roleRepository.findByRole(RoleEnum.ROLE_ADMIN).orElseThrow();

        if (profileCreateRequest.getUserId() == loggedInUser.getId() || loggedInUser.getRoles().contains(adminRole)) {
            User user = userRepository.findById(profileCreateRequest.getUserId()).orElseThrow();

            Profile newProfile = new Profile();
            newProfile.setAddress(profileCreateRequest.getAddress());
            newProfile.setHeight(profileCreateRequest.getHeight());
            newProfile.setWeight(profileCreateRequest.getWeight());

            // Set and save via User since User is the owning side
            user.setProfile(newProfile);
            userRepository.save(user);


            return ResponseEntity
                .created(URI.create(ApiPaths.PROFILE_PATH + "/" + newProfile.getId()))
                .build();
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    /*
        Executes a partial update of the corresponding profile_id. Accepts appropriate
        parameters in the request body as application/json.
    */
    @PreAuthorize("hasRole('USER') or hasRole('CONTRIBUTOR') or hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateProfile(@PathVariable long id, @Valid @RequestBody ProfilePatchRequest profilePatchRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User loggedInUser = authAdapter.getUser(authentication.getPrincipal());
        Role adminRole = roleRepository.findByRole(RoleEnum.ROLE_ADMIN).orElseThrow();

        if (loggedInUser.getProfile().getId() == id || loggedInUser.getRoles().contains(adminRole)) {
            User patchUser = userRepository.findById(id).orElseThrow();
            Profile patchProfile = patchUser.getProfile();

            if (profilePatchRequest.getHeight() != null) {
                patchProfile.setHeight(profilePatchRequest.getHeight());
            }

            if (profilePatchRequest.getWeight() != null) {
                patchProfile.setWeight(profilePatchRequest.getWeight());
            }

            if (profilePatchRequest.getAddress() != null) {
                Address patchAddress = patchProfile.getAddress();

                if (profilePatchRequest.getAddress().getAddressLine1() != null) {
                    patchAddress.setAddressLine1(profilePatchRequest.getAddress().getAddressLine1());
                }

                if (profilePatchRequest.getAddress().getAddressLine2() != null) {
                    patchAddress.setAddressLine2(profilePatchRequest.getAddress().getAddressLine2());
                }

                if (profilePatchRequest.getAddress().getAddressLine3() != null) {
                    patchAddress.setAddressLine3(profilePatchRequest.getAddress().getAddressLine3());
                }

                if (profilePatchRequest.getAddress().getCity() != null) {
                    patchAddress.setCity(profilePatchRequest.getAddress().getCity());
                }

                if (profilePatchRequest.getAddress().getCountry() != null) {
                    patchAddress.setCountry(profilePatchRequest.getAddress().getCountry());
                }

                if (profilePatchRequest.getAddress().getPostalCode() != null) {
                    patchAddress.setPostalCode(profilePatchRequest.getAddress().getPostalCode());
                }
            }

            //User is the owning side
            userRepository.save(patchUser);

            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    /*
        Deletes a profile. User self only or admin.

        NOTE: for now, this does not actually delete the profile, only the link from User.
        Final behaviour TDB.
    */
    @PreAuthorize("hasRole('USER') or hasRole('CONTRIBUTOR') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProfile(@PathVariable long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User loggedInUser = authAdapter.getUser(authentication.getPrincipal());
        Role adminRole = roleRepository.findByRole(RoleEnum.ROLE_ADMIN).orElseThrow();

        if (loggedInUser.getProfile().getId() == id || loggedInUser.getRoles().contains(adminRole)) {
            User user = userRepository.findById(id).orElseThrow();
            user.setProfile(null);
            userRepository.save(user);

            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
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
