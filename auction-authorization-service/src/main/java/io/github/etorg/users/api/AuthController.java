package io.github.etorg.users.api;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.github.etorg.users.service.AuthenticationService;
import io.github.etorg.users.service.dto.AuthenticationDto;
import io.github.etorg.users.service.dto.RegisterUserDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping({"", "/api/users/authentication"})
public class AuthController {
	
	@Autowired
	AuthenticationService authService;
	
	@PostMapping({"/signup"})
	public String signup(@RequestBody RegisterUserDto form) {
		authService.signup(form);
		return "User registered";
	}
	
	@PostMapping({"/signin"})
	public Map<String, String> signin(@RequestBody AuthenticationDto form) {
		return authService.authenticate(form);
	}
	
	@GetMapping({"/confirm-registration/{token}"})
	public ResponseEntity<Map<String, String>> confirmRegistration(@PathVariable String token){
		if (authService.confirmRegistration(token))
			return ResponseEntity.ok(Map.of("status", "success", "message", "Email confirmed"));

		return ResponseEntity.notFound().build();
	}
	
}
