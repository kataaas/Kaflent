package ru.kataaas.kaflent.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.kataaas.kaflent.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query(value = "SELECT u.id FROM users u WHERE u.username = :username", nativeQuery = true)
    Long findIdByUsername(@Param("username") String username);

    UserEntity findByUsernameOrEmail(String username, String email);

    UserEntity findByUsername(String username);

    Page<UserEntity> findAllByIdInOrderByUsernameAsc(List<Long> ids, Pageable pageable);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

}
