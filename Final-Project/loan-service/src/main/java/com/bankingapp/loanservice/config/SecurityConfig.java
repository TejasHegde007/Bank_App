package com.bankingapp.loanservice.config;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/api-docs/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                                .oauth2ResourceServer(oauth2 ->
                                                oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                                );

        return http.build();
    }

        /**
         * Custom converter that extracts both realm roles and client roles from Keycloak token
         */
        private JwtAuthenticationConverter jwtAuthenticationConverter() {
                JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
                converter.setJwtGrantedAuthoritiesConverter(this::extractAuthorities);
                return converter;
        }

        private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
                Set<String> roles = new HashSet<>();

                // Extract realm roles
                Map<String, Object> realmAccess = jwt.getClaim("realm_access");
                if (realmAccess != null && realmAccess.get("roles") instanceof Collection<?> realmRoles) {
                        for (Object role : realmRoles) {
                                roles.add(role.toString());
                        }
                }

                // Extract client roles
                Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
                if (resourceAccess != null) {
                        for (Object client : resourceAccess.values()) {
                                if (client instanceof Map<?, ?> clientMap && clientMap.get("roles") instanceof Collection<?> clientRoles) {
                                        for (Object role : clientRoles) {
                                                roles.add(role.toString());
                                        }
                                }
                        }
                }

                return roles.stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toSet());
        }
}
