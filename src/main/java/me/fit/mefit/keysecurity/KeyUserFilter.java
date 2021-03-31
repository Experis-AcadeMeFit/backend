package me.fit.mefit.keysecurity;

import me.fit.mefit.keysecurity.services.AuthAdapter;
import me.fit.mefit.models.User;
import me.fit.mefit.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(Integer.MAX_VALUE)
public class KeyUserFilter extends OncePerRequestFilter {

    Logger logger = LoggerFactory.getLogger(KeyUserFilter.class);

    @Autowired UserRepository userRepository;
    @Autowired AuthAdapter authAdapter;
    @Value("${mefit.app.usingKeycloak}") boolean usingKeycloak;

    /*
        This filter will run on every request and create
        a local user if we do not already have one.

        Counting on the caching done by JPA/Hibernate to
        not hit the DB on every request
     */

    public void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain) throws IOException, ServletException {
        logger.info("Running User Filter");
        if (usingKeycloak) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            // The principial is "anonymousUser" when we hit login and signup
            if (!auth.getPrincipal().equals( "anonymousUser") ) {
                logger.info("Checking if user is registered locally");
                User jwtUser = authAdapter.getUser(auth.getPrincipal());

                if (!userRepository.existsByKeycloakId( jwtUser.getKeycloakId())) {
                    logger.info("Registering user locally");

                    jwtUser.setPassword("maybefixthis");
                    userRepository.save(jwtUser);
                }
            }
        }

        chain.doFilter(request, response);
    }
}
