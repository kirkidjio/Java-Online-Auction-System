package io.github.etorg.users.service;

import java.util.Map;

import io.github.etorg.users.service.events.UserRegisteredEvent;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.github.etorg.users.infrastructure.UserRepository;
import io.github.etorg.users.models.User;
import io.github.etorg.security.JwtService;
import io.github.etorg.users.service.dto.AuthenticationDto;
import io.github.etorg.users.service.dto.RegisterUserDto;

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
	RabbitTemplate rabbitTemplate;

	@Autowired
	DirectExchange directExchange;
	
	
	
	public void signup(RegisterUserDto input) {
		User user = new User();
		user.setEmail(input.email());
		user.setPassword(passwordEncoder.encode(input.password()));
		user.setUsername(input.username());
		
		userRepository.save(user);

		rabbitTemplate.convertAndSend(directExchange.getName(), "routing.users.notifications",
				new UserRegisteredEvent(user.getId(), user.getUsername(), user.getEmail()));
	}
	
	public Map<String, String> authenticate(AuthenticationDto input) {
		
		authManager.authenticate(new UsernamePasswordAuthenticationToken(input.username(), input.password()));
		User user = userRepository.findByUsername(input.username()).orElseThrow();
		return Map.of("jwt" ,jwtService.buildToken(user.getId()));
		
		
		
	}
}
