package com.example.springsecurityjwt.services;

import com.example.springsecurityjwt.Repository.PhotoFolderRepository;
import com.example.springsecurityjwt.models.PhotoFolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhotoFolderService {

    @Autowired
    PhotoFolderRepository photoFolderRepository;

    public PhotoFolder createFolder(PhotoFolder folder){
        return photoFolderRepository.save(folder);
    }

    public List<PhotoFolder> findByUserName(String userName){
        return photoFolderRepository.findAllByUserName(userName);
    }

    public PhotoFolder findByUserNameAndFolderName(String userName, String folderName){
        return photoFolderRepository.findByUserNameAndFolderName(userName, folderName);
    }

    public List<PhotoFolder> getAllFolders(String userName){
        return photoFolderRepository.getAllFolders(userName);
    }
}
