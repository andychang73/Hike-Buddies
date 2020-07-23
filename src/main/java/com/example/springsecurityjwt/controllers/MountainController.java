package com.example.springsecurityjwt.controllers;

import com.example.springsecurityjwt.models.Mountain;
import com.example.springsecurityjwt.services.MountainService;
import com.example.springsecurityjwt.util.FilesUtil;
import com.example.springsecurityjwt.util.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/mountain")
public class MountainController {

    private final String mountainPath = "mountains";

    public static final String vmAddress = "http://35.194.165.190:80/mountains/";

    //public static final String vmAddress = "http://35.194.165.190:80/home/andy_chang73/final/mountain/";

    @Autowired
    private MountainService mountainService;

    @Autowired
    private FilesUtil filesUtil;

    @PostMapping("/")
    public Status getMountainName(@RequestBody List<Integer> mountainIds) {
        List<Mountain> list = new ArrayList<>();
        mountainIds.forEach((integer -> list.add(mountainService.findByMountainId(integer))));
        if (list == null) {
            return new Status(400, "Mountains does not exist!");
        }
        list.forEach((Mountain mountain)-> {
            mountain.setPhotoPath(vmAddress+mountain.getPhotoPath());
        });

        return new Status(200, list);
    }

    @PostMapping("/uploadPhoto")
    public Status uploadPhoto(@RequestParam("file") MultipartFile file,
                              @RequestParam("mountainId") int mountainId) {
        Mountain mountain = mountainService.findByMountainId(mountainId);
        if (mountain.getPhotoPath() != null) {
            if (filesUtil.delete("/"+mountainPath+"/"+mountain.getPhotoPath()).getStatus() != 200) {
                return new Status(404, "Could not delete file");
            }
        }
        Status status = filesUtil.uploadFile(file, mountainPath);
        if (status.getStatus() != 200) {
            return status;
        }
        mountain.setPhotoPath(status.getMsg());
        mountainService.save(mountain);
        return new Status(200, status.getMsg());
    }

    @GetMapping("/loadPhoto/{id}")
    public Status loadPhoto(@PathVariable int id) {
        Mountain mountain = mountainService.findByMountainId(id);
        if (mountain == null) {
            return new Status(404, "Data not found");
        }
        String path = vmAddress + mountain.getPhotoPath();
        return new Status(200, path);

    }

    @GetMapping("/keyword/{keyword}")
    public Status keywordSearch(@PathVariable String keyword){
        List<Mountain> mountains = mountainService.keywordSearch(keyword);
        mountains.forEach((Mountain m)->{
            m.setAchievements(null);
            m.setEvents(null);
        });
        return new Status(200, mountains);
    }

    @GetMapping("/findMostReached")
    public Status findMostReached(){
        List<Mountain> mostReached = mountainService.findMostReached();
        mostReached.forEach((Mountain m)->{
            m.setPhotoPath(MountainController.vmAddress+m.getPhotoPath());
            m.setEvents(null);
            m.setAchievements(null);
        });
        return new Status (200, mostReached);
    }
}
