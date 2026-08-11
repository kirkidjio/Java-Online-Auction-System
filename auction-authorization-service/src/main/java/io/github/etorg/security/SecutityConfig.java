package io.github.etorg.security;

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

/**
 * Shared security configuration of the application.
 *
 * Each module owns its own filter chain, limited to its own securityMatcher,
 * so when the module is extracted into a microservice, its chain moves along
 * with it without requiring changes to shared configurations.
 *
 * Authorization is performed only at the endpoint (URL) level: method security
 * (@EnableMethodSecurity / @PreAuthorize) is not used for now.
 */
@EnableWebSecurity
@Configuration
public class SecutityConfig {
	
	@Autowired
	AuthenticationProvider authProvider;
	
	@Autowired
	JwtFilter jwtFilter;
	

	
	/**
	 * Generic fallback chain (not owned by any module): everything that is not
	 * described by the module chains and is not public infrastructure (swagger)
	 * is closed behind authentication. After the split, each microservice keeps it.
	 */
	@Bean
	@Order(3)
	SecurityFilterChain fallbackSecurityFilterChain(HttpSecurity http) throws Exception{
		return http
				.securityMatcher("/**")
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
						.anyRequest().authenticated())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.csrf(csrf -> csrf.disable())
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
				.build();
	}
}
