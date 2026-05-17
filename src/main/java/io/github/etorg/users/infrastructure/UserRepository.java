package io.github.etorg.users.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import io.github.etorg.users.models.User;

@Repository
public interface UserRepository extends CrudRepository<User, UUID> {
	Optional<User> findByEmail(String email);
	Optional<User> findById(UUID id);
}
