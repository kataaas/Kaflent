package ru.kataaas.kaflent.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kataaas.kaflent.entity.RoleEntity;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    RoleEntity findByName(String name);

}
