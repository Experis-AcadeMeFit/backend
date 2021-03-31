package me.fit.mefit.keysecurity.services;

import me.fit.mefit.models.Role;
import me.fit.mefit.models.RoleEnum;
import me.fit.mefit.models.User;
import me.fit.mefit.repositories.RoleRepository;
import me.fit.mefit.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AuthAdapter {

    @Autowired RoleRepository roleRepository;
    @Autowired UserRepository userRepository;

    Logger logger = LoggerFactory.getLogger(AuthAdapter.class);

    /*
        Return a User based on the environment we're in (local auth or keycloak).
        If the user exists in the local db, then return that user, otherwise return a
        "transient" User based on the claims in the jwt

        Relying on keycloak as the authority to not have things like email collisions
    */
    public User getUser(Object principal) {
        if (principal instanceof UserDetails) {
            return userRepository.findByEmail( ((UserDetails) principal).getUsername()).orElseThrow();
        } else if (principal instanceof Jwt) {
            Jwt jwtPrincipal = (Jwt) principal;
            User jwtUser = createUserFromJwt(jwtPrincipal);

            if (userRepository.existsByKeycloakId(jwtUser.getKeycloakId())) {
                User existingUser = userRepository.findByKeycloakId(jwtUser.getKeycloakId()).orElseThrow();

                // Sync up roles, keycloak is the authority

                // add roles that are in the jwt but not in our db
                jwtUser.getRoles().forEach(role -> {
                    if (!existingUser.getRoles().contains(role)) {
                        logger.info("Added role to user from jwt: {}" , role);
                        existingUser.getRoles().add(role);
                    }
                });

                // remove roles that are in our db but not the jwt
                existingUser.getRoles().forEach(role -> {
                    if (!jwtUser.getRoles().contains(role)) {
                        logger.info("Removed role from user based on jwt: {}", role);
                    }
                });

                userRepository.save(existingUser);
                return existingUser;
            }
            return jwtUser;
        }

        throw new IllegalArgumentException("Unknown principal type: " + principal.toString());
    }

    private User createUserFromJwt(Jwt jwt) {
        User user = new User();
        user.setFirstName((String) jwt.getClaims().get("given_name"));
        user.setLastName((String) jwt.getClaims().get("family_name"));
        user.setEmail((String) jwt.getClaims().get("email"));
        user.setKeycloakId(jwt.getSubject());

        //TODO: make more robust
        Set<Role> roles = new HashSet<>();
        List<String> claimRoles = jwt.getClaim("roles");
        claimRoles.forEach( claimRole -> {
            RoleEnum roleEnum = RoleEnum.valueOf(claimRole);
            if (roleRepository.existsByRole(roleEnum)) {
                Role role = roleRepository.findByRole(roleEnum).orElseThrow();
                roles.add(role);
            }
        });

        user.setRoles(roles);

        return user;
    }
}
