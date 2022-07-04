package ru.kataaas.kaflent.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kataaas.kaflent.entity.FileEntity;

public interface FileRepository extends JpaRepository<FileEntity, Long> {

}
