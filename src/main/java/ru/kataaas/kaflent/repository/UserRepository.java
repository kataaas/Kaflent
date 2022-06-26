package ru.kataaas.kaflent.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kataaas.kaflent.entity.UserEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByUsernameOrEmail(String username, String email);

    UserEntity findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

}
