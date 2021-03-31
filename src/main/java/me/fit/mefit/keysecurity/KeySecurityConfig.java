package me.fit.mefit.keysecurity;

import me.fit.mefit.utils.ApiPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(1)
public class KeySecurityConfig extends WebSecurityConfigurerAdapter {
    Logger logger = LoggerFactory.getLogger(me.fit.mefit.security.WebSecurityConfig.class);

    @Value("${mefit.app.usingKeycloak}") boolean usingKeycloak;

    // Convert the claims from Keycloak into something Spring understands
    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
        converter.setAuthoritiesClaimName("roles");
        converter.setAuthorityPrefix("");
        JwtAuthenticationConverter authConverter = new JwtAuthenticationConverter();
        authConverter.setJwtGrantedAuthoritiesConverter(converter);
        return authConverter;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        if (usingKeycloak) {
            http.cors()
                    .and()
                    .csrf().disable()
                    .sessionManagement().sessionCreationPolicy( SessionCreationPolicy.STATELESS )
                    .and()
                    .authorizeRequests().antMatchers( ApiPaths.LOGIN_PATH + "/**" ).permitAll()
                    .antMatchers( HttpMethod.POST, ApiPaths.USER_PATH ).permitAll()
                    .antMatchers( ApiPaths.PROTECTED_PATH ).permitAll().anyRequest().authenticated()
                    .and()
                    .oauth2ResourceServer()
                    .jwt()
                    .jwtAuthenticationConverter( jwtAuthenticationConverter() );
        }
    }
}

