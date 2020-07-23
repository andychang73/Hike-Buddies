package com.example.springsecurityjwt.Repository;

import com.example.springsecurityjwt.models.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Integer> {

    @Query(
            value = "SELECT photo_path FROM photo WHERE folder_id = ?1 ORDER BY post_date DESC",
            nativeQuery = true
    )
    List<String> findAllByFolderId(int folderId);

    @Query(
            value = "SELECT photo_path FROM photo WHERE user_name = ?1 ORDER BY post_date DESC",
            nativeQuery = true
    )
    List<String> findAllByUserName(String userName);

    @Query(
            value = "SELECT COUNT(*) FROM photo WHERE folder_id = ?1",
            nativeQuery = true
    )
    int getPhotoCount(int folderId);

    @Query(
            value = "SELECT photo_path FROM photo WHERE folder_id = ?1 LIMIT 1",
            nativeQuery = true
    )
    String getFirstPhoto(int folderId);
}
