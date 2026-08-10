package io.github.etorg.users.service.events;

import java.util.UUID;

public record UserRegisteredEvent(UUID userId, String username , String email) {}
