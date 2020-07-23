package com.example.springsecurityjwt.Repository;

import com.example.springsecurityjwt.models.Achievement;
import com.example.springsecurityjwt.models.Mountain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Integer> {

    @Query(
            value ="SELECT DISTINCT mountain_id FROM achievement WHERE user_name = ?1",
            nativeQuery = true
    )
    List<Integer> climbedMountainIds(String userName);

    @Query(
            value = "SELECT mountain_id FROM achievement WHERE user_name = ?1",
            nativeQuery = true
    )
    List<Integer> achievementRecords (String userName);
}
