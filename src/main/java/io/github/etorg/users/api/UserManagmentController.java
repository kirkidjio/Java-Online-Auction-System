package io.github.etorg.users.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.etorg.users.infrastructure.UserRepository;
import io.github.etorg.users.service.UserManagmentService;
import io.github.etorg.users.service.dto.ChangeRoleDto;
import io.github.etorg.users.service.dto.DeleteUserDto;


@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserManagmentController {
	
	@Autowired
	UserManagmentService service;
	
	
	@PostMapping("/change-role")
	public int changeRole(@RequestBody ChangeRoleDto form) {
		service.changeRole(form);
		return 201;
	}
	
	@DeleteMapping("/delete")
	public int deleteUser(@RequestBody DeleteUserDto form) {
		service.deleteUser(form);
		return 201;
	}
	
	
}
