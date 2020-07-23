package com.example.springsecurityjwt.Repository;

import com.example.springsecurityjwt.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUserName(String userName);

    @Query(
            value = "SELECT * FROM user",
            nativeQuery = true
    )
    List<User> findAllUser();

    @Query(
        value = "SELECT * FROM user WHERE user_name = ?1",
        nativeQuery = true
    )
    User getUserInfo(String userName);

    @Query(
            value = "SELECT name FROM user WHERE user_name = ?1",
            nativeQuery = true
    )
    String getName(String userName);

    @Query(
            value = "SELECT photo_path FROM user WHERE user_name = ?1",
            nativeQuery = true
    )
    String getPhotoPath(String userName);

    @Query(
            value = "SELECT user_id FROM user WHERE user_name = ?1",
            nativeQuery = true
    )
    int getUserId(String userName);

    @Query(
            value = "SELECT * FROM user WHERE user_id = ?1",
            nativeQuery = true
    )
    User findByUserId(int userId);

    @Query(
            value = "SELECT user_name FROM user WHERE user_id = ?1",
            nativeQuery = true
    )
    String getUserName(int userId);
}
