package io.github.etorg.users.infrastructure;

import io.github.etorg.users.models.UserCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCacheRepository extends CrudRepository <UserCache, String> {
    Optional<UserCache> findById(String token);
    void deleteById(String token);
}
