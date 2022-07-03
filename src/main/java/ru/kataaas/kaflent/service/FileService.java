package ru.kataaas.kaflent.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kataaas.kaflent.entity.FileEntity;
import ru.kataaas.kaflent.repository.FileRepository;
import ru.kataaas.kaflent.utils.FileTypeEnum;

@Service
public class FileService {

    private final FileRepository fileRepository;

    @Autowired
    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public FileEntity save(FileEntity file) {
        return fileRepository.save(file);
    }

    public FileEntity findByOwnerIdAndType(Long id, String type) {
        return fileRepository.findByOwnerIdAndType(id, FileTypeEnum.valueOf(type));
    }

}
