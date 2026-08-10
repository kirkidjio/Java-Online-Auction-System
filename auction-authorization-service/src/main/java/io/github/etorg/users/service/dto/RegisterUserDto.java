package io.github.etorg.users.service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterUserDto(

        @NotBlank
        @Email
        String email,

        @NotBlank
        @Size(min = 4, max = 20)
        @Pattern(regexp = "^[a-zA-Z0-9_]+$")
        String username,

        @NotBlank
        @Size(min = 8, max = 100)
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).+$"
        )
        String password

) {
}
