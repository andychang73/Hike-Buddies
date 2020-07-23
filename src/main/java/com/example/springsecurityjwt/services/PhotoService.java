package com.example.springsecurityjwt.services;

import com.example.springsecurityjwt.Repository.PhotoRepository;
import com.example.springsecurityjwt.models.Photo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhotoService {

    @Autowired
    PhotoRepository photoRepository;

    public Photo uploadPhoto(Photo photo){
        return photoRepository.save(photo);
    }

    public List<String> findAllByFolderId(int folderId){
        return photoRepository.findAllByFolderId(folderId);
    }

    public List<String> findAllByUserName(String userName){
        return photoRepository.findAllByUserName(userName);
    }

    public int getPhotoCount(int folderId){
        return photoRepository.getPhotoCount(folderId);
    }

    public String getFirstPhoto(int folderId){
        return photoRepository.getFirstPhoto(folderId);
    }
}
