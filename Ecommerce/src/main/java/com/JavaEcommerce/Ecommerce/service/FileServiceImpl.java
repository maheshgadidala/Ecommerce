package com.JavaEcommerce.Ecommerce.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService{

    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {

        String originalFileName = file.getOriginalFilename(); // Fixed method name

        String randomId = UUID.randomUUID().toString();

        // Safe extension extraction
        String filename;
        if (originalFileName != null) {
            int dotIndex = originalFileName.lastIndexOf(".");
            if (dotIndex != -1) {
                filename = randomId.concat(originalFileName.substring(dotIndex));
            } else {
                filename = randomId; // or add default extension
            }
        } else {
            filename = randomId; // fallback for null filename
        }

        String filePath = path + File.separator + filename; // Fixed separator

        // Check if folder exists, if not create
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs(); // Use mkdirs() to create parent directories too
        }

        // Upload to server - use full path
        Files.copy(file.getInputStream(), Paths.get(filePath)); // Fixed path
        return filename;
    }
}
