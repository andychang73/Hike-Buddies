package com.example.springsecurityjwt.controllers;

import com.example.springsecurityjwt.models.Photo;
import com.example.springsecurityjwt.models.PhotoFolder;
import com.example.springsecurityjwt.models.User;
import com.example.springsecurityjwt.services.MyUserDetailsService;
import com.example.springsecurityjwt.services.PhotoFolderService;
import com.example.springsecurityjwt.services.PhotoService;
import com.example.springsecurityjwt.util.FilesUtil;
import com.example.springsecurityjwt.util.JwtUtil;
import com.example.springsecurityjwt.util.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/photo")
public class PhotoController {

    private final String photoPath = "photo";

    public static final String vmAddress = "http://35.194.165.190:80/photo/";

    @Autowired
    PhotoService photoService;

    @Autowired
    PhotoFolderService photoFolderService;

    @Autowired
    MyUserDetailsService userDetailsService;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    FilesUtil filesUtil;

    @PostMapping("/upload")
    public Status uploadPhoto(@RequestParam("files")List<MultipartFile> files,
                              @RequestParam("folder") String folder,
                              @RequestHeader("Authorization") String jwt){
        String userName = jwtUtil.extractUsername(jwt);
        Optional<User> existingUser = userDetailsService.findByUsername(userName);
        if(existingUser.isEmpty()){
            return new Status(400, "User does not exist!");
        }
        PhotoFolder photoFolder = photoFolderService.findByUserNameAndFolderName(userName, folder);
        if(photoFolder == null){
            return new Status (401, "Folder does not exist!");
        }
        List<Photo> photos = new ArrayList<>();
        for (MultipartFile file : files) {
            Status status = filesUtil.uploadFile(file, photoPath);
            if (status.getStatus() != 200) {
                return status;
            }
            Photo photo = new Photo(status.getMsg(), photoFolder.getFolderId(), userName);
            photos.add(photo);
            photoService.uploadPhoto(photo);
        }
        return new Status (200, photos);
    }

    @GetMapping("/getAll/{userName}")
    public Status getAllPhotos(@PathVariable String userName){
        List<String> photos = photoService.findAllByUserName(userName);
        if(photos.isEmpty()){
            return new Status (400, "No photos!");
        }
        List<String> addVmAddress = new ArrayList<>();
        photos.forEach((String path)-> addVmAddress.add(vmAddress+path));
        return new Status (200, addVmAddress);
    }

    @GetMapping("/getFolderPhotos/{userName}/{folderName}")
    public Status getFolderPhotos(@PathVariable String userName,
                                  @PathVariable String folderName){
        PhotoFolder folder = photoFolderService.findByUserNameAndFolderName(userName, folderName);
        if(folder == null){
            return new Status (401, "Folder does not exist!");
        }
        List<String> photos = photoService.findAllByFolderId(folder.getFolderId());
        if(photos.isEmpty()){
            return new Status (402, "No photos!");
        }
        List<String> addVmAddress = new ArrayList<>();
        photos.forEach((String path)-> addVmAddress.add(vmAddress+path));
        return new Status (200, addVmAddress);
    }
}
