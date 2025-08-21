// src/main/java/com/example/demo/services/Interface/FileStorageService.java
package com.example.demo.services.Interface;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String save(MultipartFile file);
    byte[] load(String path);
    void delete(String path);
}
