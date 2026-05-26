package io.github.etorg.users.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.etorg.users.service.AuthenticationService;
import io.github.etorg.users.service.dto.AuthenticationDto;
import io.github.etorg.users.service.dto.RegisterUserDto;

@RestController
@RequestMapping("api/users/authentication/")
public class AuthController {
	
	@Autowired
	AuthenticationService authService;
	
	@PostMapping("signup/")
	public String signup(@RequestBody RegisterUserDto form) {
		authService.signup(form);
		return "User registered";
	}
	
	@PostMapping("signin/")
	public void signin(@RequestBody AuthenticationDto form) {
		authService.authenticate(form);
	}
	
	
	
}
