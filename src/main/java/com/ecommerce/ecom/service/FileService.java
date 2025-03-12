package com.ecommerce.ecom.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import static com.ecommerce.ecom.config.AppConstants.IMAGES_PATH;

@Service
public class FileService {
    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        assert fileName != null;
        String newFileName = UUID.randomUUID().toString().concat(fileName.substring(fileName.lastIndexOf(".")));
        String filePath = IMAGES_PATH +  File.separator + newFileName;

        File folder = new File(IMAGES_PATH);
        if (!folder.exists()) {
            folder.mkdir();
        }

        Files.copy(file.getInputStream(), Paths.get(filePath));
        return newFileName;
    }
}
