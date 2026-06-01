package io.github.etorg.users.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

import io.github.etorg.users.infrastructure.UserRepository;
import io.github.etorg.users.models.User;
import io.github.etorg.users.security.JwtService;
import io.github.etorg.users.service.dto.AuthenticationDto;
import io.github.etorg.users.service.dto.RegisterUserDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthenticationService {
	
	@Autowired
	AuthenticationManager authManager;
	
	@Autowired
	JwtService jwtService;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	SecurityContextRepository securityContextRepository;
	
	public void signup(RegisterUserDto input) {
		User user = new User();
		user.setEmail(input.email());
		user.setPassword(passwordEncoder.encode(input.password()));
		user.setUsername(input.username());
		
		userRepository.save(user);
	}
	
	public void authenticate(AuthenticationDto input,
	        HttpServletRequest request,
	        HttpServletResponse response) {
		
		Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(input.username(), input.password()));
		
		SecurityContext context = SecurityContextHolder.createEmptyContext();
	    context.setAuthentication(auth);
	    SecurityContextHolder.setContext(context);

	    securityContextRepository.saveContext(context, request, response);
		
		//return jwtService.buildToken(user.getId());
		
	}
}
