package com.example.springsecurityjwt.util;

import com.example.springsecurityjwt.services.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Component
public class FilesUtil {

    @Autowired
    FileStorageService filesStorageService;

    public Status uploadFile(MultipartFile file, String filePath){
        try{
            return filesStorageService.save(file, filePath);
        }catch(Exception e){
            return new Status(404, e.getMessage());
        }
    }

//    public Status uploadFiles(MultipartFile files, String filePath){
//        try{
//            List<String> fileNames = new ArrayList<>();
//            Iterator<MultipartFile> a = Arrays.asList(files).iterator();
//            while(a.hasNext()){
//                MultipartFile file = a.next();
//
//                Status status = filesStorageService.save(file, filePath);
//                if(status.getStatus() != 200){
//                    return new Status (400, "Could not upload file: "+ file);
//                }
//                fileNames.add(file.getOriginalFilename());
//            }
//            return new Status (200, fileNames);
//        }catch(Exception e){
//            return new Status(404, e.getMessage());
//        }
//    }

    public Status delete(String filename){
        return filesStorageService.delete(filename);
    }

    @GetMapping("/static/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable String filename){
        Resource file = filesStorageService.load(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
}
