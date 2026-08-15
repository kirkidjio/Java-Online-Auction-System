package io.github.etorg.users.service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

import io.github.etorg.users.infrastructure.UserCacheRepository;
import io.github.etorg.users.models.UserCache;
import io.github.etorg.users.service.events.UserConfirmRegistrationEvent;
import io.github.etorg.users.service.events.UserRegisteredEvent;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

	@Autowired
	UserCacheRepository userCacheRepository;
	
	
	
	public void signup(RegisterUserDto input) {
		UserCache user = new UserCache();
		user.setToken(generateRegistrationToken());
		user.setEmail(input.email());
		user.setPassword(passwordEncoder.encode(input.password()));
		user.setUsername(input.username());
		
		userCacheRepository.save(user);

		rabbitTemplate.convertAndSend(directExchange.getName(), "routing.users.confirm-registration",
				new UserConfirmRegistrationEvent(user.getEmail(), user.getToken()));
	}
	
	public Map<String, String> authenticate(AuthenticationDto input) {
		
		authManager.authenticate(new UsernamePasswordAuthenticationToken(input.username(), input.password()));
		User user = userRepository.findByUsername(input.username()).orElseThrow();
		return Map.of("jwt" ,jwtService.buildToken(user.getId()));

		
	}

	public boolean confirmRegistration(String token) {
		Optional<UserCache> userCacheOptional = userCacheRepository.findById(token);


		if (userCacheOptional.isPresent()){
			UserCache userCache = userCacheOptional.get();
			User user = new User();

			user.setUsername(userCache.getUsername());
			user.setEmail(userCache.getEmail());
			user.setPassword(userCache.getPassword());

			userRepository.save(user);
			userCacheRepository.deleteById(userCache.getToken());
			rabbitTemplate.convertAndSend(directExchange.getName(), "routing.users.registered",
					new UserRegisteredEvent(user.getId(), user.getUsername(), user.getEmail()));


			return true;
		}

		return false;
	}

	private String generateRegistrationToken(){
		SecureRandom secureRandom = new SecureRandom();
		Base64.Encoder base64Encoder = Base64.getUrlEncoder();

		byte[] randomBytes = new byte[64];
		secureRandom.nextBytes(randomBytes);

		return base64Encoder.encodeToString(randomBytes);
	}
}
