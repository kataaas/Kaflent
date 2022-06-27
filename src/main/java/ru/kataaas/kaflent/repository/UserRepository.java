package ru.kataaas.kaflent.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.kataaas.kaflent.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByUsernameOrEmail(String username, String email);

    UserEntity findByUsername(String username);

    Page<UserEntity> findAllByIdInOrderByUsernameAsc(List<Long> ids, Pageable pageable);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

}
