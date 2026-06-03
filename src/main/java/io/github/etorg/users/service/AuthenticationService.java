package io.github.etorg.users.service;

import java.util.Map;

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
	
	
	
	public void signup(RegisterUserDto input) {
		User user = new User();
		user.setEmail(input.email());
		user.setPassword(passwordEncoder.encode(input.password()));
		user.setUsername(input.username());
		
		userRepository.save(user);
	}
	
	public Map<String, String> authenticate(AuthenticationDto input) {
		
		authManager.authenticate(new UsernamePasswordAuthenticationToken(input.username(), input.password()));
		User user = userRepository.findByUsername(input.username()).orElseThrow();
		return Map.of("jwt" ,jwtService.buildToken(user.getId()));
		
		
		
	}
}
