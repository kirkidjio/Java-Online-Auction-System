package io.github.etorg.users.service.events;

public record UserConfirmRegistrationEvent (String email, String token) {
}
