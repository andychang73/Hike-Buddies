package com.example.springsecurityjwt.controllers;

import com.example.springsecurityjwt.dto.FolderWithPhotoCount;
import com.example.springsecurityjwt.models.PhotoFolder;
import com.example.springsecurityjwt.services.PhotoFolderService;
import com.example.springsecurityjwt.services.PhotoService;
import com.example.springsecurityjwt.util.JwtUtil;
import com.example.springsecurityjwt.util.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/photoFolder")
public class PhotoFolderController {

    @Autowired
    PhotoFolderService photoFolderService;

    @Autowired
    PhotoService photoService;

    @Autowired
    JwtUtil jwtUtil;

    @PostMapping("/create")
    public Status createFolder(@RequestHeader("Authorization") String jwt, @RequestBody PhotoFolder folder){
        String userName = jwtUtil.extractUsername(jwt);
        folder.setUserName(userName);
        List<PhotoFolder> folders = photoFolderService.findByUserName(userName);
        if(folders.isEmpty()){
            PhotoFolder photoFolder = photoFolderService.createFolder(folder);
            return new Status (200, photoFolder);
        }
        for (PhotoFolder value : folders) {
            if (value.getFolderName().equals(folder.getFolderName())) {
                return new Status(400, "Folder already exists!");
            }
        }
        PhotoFolder photoFolder = photoFolderService.createFolder(folder);
        return new Status (200, photoFolder);
    }

    @GetMapping("/allFolders/{userName}")
    public Status getAllFolders(@PathVariable String userName){
        List<PhotoFolder> folders = photoFolderService.getAllFolders(userName);
        if(folders.isEmpty()){
            return new Status(200, "No folders!");
        }
        List<FolderWithPhotoCount> list = new ArrayList<>();
        folders.forEach((PhotoFolder p)->{
            p.setPhotos(null);
            list.add(new FolderWithPhotoCount(p,
                    photoService.getPhotoCount(p.getFolderId()),
                    PhotoController.vmAddress+photoService.getFirstPhoto(p.getFolderId())));
        });
        return new Status (200, list);
    }

}
