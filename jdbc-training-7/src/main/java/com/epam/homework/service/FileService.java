package com.epam.homework.service;

import com.epam.homework.config.AppConfig;
import com.epam.homework.model.File;
import com.epam.homework.repository.FileRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

public class FileService {
    private final FileRepository fileRepository;

    public FileService() {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        this.fileRepository = context.getBean(FileRepository.class);;
    }

    public File upload(File file) {
        return fileRepository.save(file);
    }

    public File getFileById(Long id) {
        return fileRepository.getById(id.intValue());
    }
}
