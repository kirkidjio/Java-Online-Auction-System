package io.github.etorg.users.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecutityConfig {
	
	@Autowired
	AuthenticationProvider authProvider;
	
	@Autowired
	JwtFilter jwtFilter;
	
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		return http
				
				.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
				.authenticationProvider(authProvider)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.csrf(csrf -> csrf.disable())
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
				.build();
				
	}
	

	
	
	
}
