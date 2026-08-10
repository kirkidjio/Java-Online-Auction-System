package io.github.etorg.users.service.dto;

import io.github.etorg.users.service.enums.Roles;

public record ChangeRoleDto(String username, Roles role) {

}
