package io.github.etorg.users.models;

import jakarta.persistence.Column;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@RedisHash(value = "users", timeToLive = 1600)
public class UserCache {
    @Id
    private String token;

    private String username;

    private String email;

    private String password;

    private String role = "ROLE_USER";
}
