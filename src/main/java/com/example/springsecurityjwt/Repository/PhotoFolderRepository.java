package com.example.springsecurityjwt.Repository;

import com.example.springsecurityjwt.models.PhotoFolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoFolderRepository extends JpaRepository<PhotoFolder, Integer> {

    List<PhotoFolder> findAllByUserName(String userName);

    @Query(
            value = "SELECT * FROM photo_folder WHERE user_name = ?1 AND folder_name = ?2 LIMIT 1",
            nativeQuery = true
    )
    PhotoFolder findByUserNameAndFolderName(String userName, String folderName);

    @Query(
            value = "SELECT * FROM photo_folder WHERE user_name = ?1",
            nativeQuery = true
    )
    List<PhotoFolder> getAllFolders(String userName);
}
