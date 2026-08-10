package io.github.etorg.users.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.etorg.users.infrastructure.UserRepository;
import io.github.etorg.users.models.User;
import io.github.etorg.users.service.dto.ChangeRoleDto;
import io.github.etorg.users.service.dto.DeleteUserDto;
import io.github.etorg.users.service.enums.Roles;

@Service
public class UserManagmentService {
	
	@Autowired
	UserRepository rep;
	
	public void changeRole(ChangeRoleDto dto) {
		User user = rep.findByUsername(dto.username()).orElseThrow();
		user.setRole(dto.role().name());
		rep.save(user);
	}
	
	public void deleteUser(DeleteUserDto dto) {
		rep.deleteByUsername(dto.username());
	}
	
	
}
