package com.example.springsecurityjwt.Repository;

import com.example.springsecurityjwt.util.Status;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface FilesStorageRepository {
    void init();

    Status save(MultipartFile file, String filePath);

    Resource load(String filename);

    void deleteAll();

    Status delete(String fileName);

    Stream<Path> loadAll();
}
