package ru.kataaas.kaflent.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kataaas.kaflent.utils.FileNameGenerator;
import ru.kataaas.kaflent.utils.StaticVariable;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@Service
public class StorageService {

    private final FileService fileService;

    private final FileNameGenerator fileNameGenerator;

    @Autowired
    public StorageService(FileService fileService, FileNameGenerator fileNameGenerator) {
        this.fileService = fileService;
        this.fileNameGenerator = fileNameGenerator;
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(StaticVariable.FILE_STORAGE_PATH));
        } catch (IOException e) {
            log.error("Cannot initialize directory : {}", e.getMessage());
        }
    }

}
