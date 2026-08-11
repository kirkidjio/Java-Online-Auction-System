package io.github.etorg.users;

import io.github.etorg.security.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
public class UsersSecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private AuthenticationProvider authProvider;

    @Bean
    @Order(1)
    SecurityFilterChain usersSecurityFilterChain(HttpSecurity http) throws Exception{
        return http
                .securityMatcher("/api/users/**", "/api/admin/**", "/signup", "/signin")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/authentication/signup", "/api/users/authentication/signin", "/signup", "/signin").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .authenticationProvider(authProvider)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.disable())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


}
