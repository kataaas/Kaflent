package ru.kataaas.kaflent.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kataaas.kaflent.entity.FileEntity;
import ru.kataaas.kaflent.utils.FileTypeEnum;

public interface FileRepository extends JpaRepository<FileEntity, Long> {

    FileEntity findByOwnerIdAndType(Long ownerId, FileTypeEnum type);

}
