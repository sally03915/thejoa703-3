package com.thejoa703.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class UtilUpload  { 
    @Value("${resource.path}") 
    private String resourcePath; 

    private Path root;

    @PostConstruct
    public void init() {
        root = Paths.get(resourcePath);
        try {
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }
        } catch (IOException e) {
            throw new RuntimeException("업로드 디렉토리 생성 실패", e);
        }
    }

    public String fileUpload(MultipartFile file) throws IOException {
        UUID uid = UUID.randomUUID();
        String save = uid.toString() + "_" + file.getOriginalFilename();
        File target = new File(resourcePath, save);
        file.transferTo(target); // 대용량 파일에 더 안전
        return save;
    }
}
