package com.example.springsecurityjwt.controllers;

import com.example.springsecurityjwt.models.FileInfo;
import com.example.springsecurityjwt.services.FileStorageService;
import com.example.springsecurityjwt.util.FilesUtil;
import com.example.springsecurityjwt.util.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/allFiles")
    public ResponseEntity<Status> getListFiles() {
        List<FileInfo> fileInfos = fileStorageService.loadAll().map(path -> {
            String filename = path.getFileName().toString();
            String url = MvcUriComponentsBuilder
                    .fromMethodName(FilesUtil.class, "getFile", path.getFileName().toString()).build().toString();
            return new FileInfo(filename, url);
        }).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(new Status(200, fileInfos));
    }
}
