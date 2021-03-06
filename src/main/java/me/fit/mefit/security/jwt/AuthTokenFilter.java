package me.fit.mefit.security.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

public class AuthTokenFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Autowired private JwtUtils jwtUtils;

    @Qualifier("userDetailsServiceImplementation")
    @Autowired private UserDetailsService userDetailsService;

    @Value("${mefit.app.usingKeycloak}") boolean usingKeycloak;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (!usingKeycloak) {
            try {
                String jwt = parseJwt( request );
                if (jwt != null && jwtUtils.validateJwtToken( jwt )) {
                    String email = jwtUtils.getEmailFromJwtToken( jwt );

                    UserDetails userDetails = userDetailsService.loadUserByUsername( email );
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authenticationToken.setDetails( new WebAuthenticationDetailsSource().buildDetails( request ) );

                    SecurityContextHolder.getContext().setAuthentication( authenticationToken );
                }
            } catch (Exception e) {
                logger.error("Cannot set user authentication: {}", e.getMessage());
            }
        }

        filterChain.doFilter( request, response );
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader( "Authorization" );

        if ( StringUtils.hasText( headerAuth ) && headerAuth.startsWith(" Bearer " ) ) {
            return headerAuth.substring( 7 );
        }

        return null;
    }
}
