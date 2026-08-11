package io.github.etorg.lot.internal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import io.github.etorg.security.JwtFilter;

/**
 * Security configuration of the lots module.
 *
 * The chain serves only /api/lots/** paths; everything inside requires authentication.
 * When the module is extracted into a microservice, this class moves along with it.
 * Authorization is performed only at the endpoint (URL) level.
 */
@Configuration
public class LotsSecurityConfig {
	
	@Autowired
	JwtFilter jwtFilter;
	
	/**
	 * Chain of the lots module: any request to /api/lots/** is allowed
	 * only for authenticated users.
	 */
	@Bean
	@Order(2)
	SecurityFilterChain lotsSecurityFilterChain(HttpSecurity http) throws Exception{
		return http
				.securityMatcher("/api/lots/**")
				.authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.csrf(csrf -> csrf.disable())
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
				.build();
	}
}
