package com.example.springsecurityjwt.services;

import com.example.springsecurityjwt.Repository.AchievementRepository;
import com.example.springsecurityjwt.models.Achievement;
import com.example.springsecurityjwt.models.Mountain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AchievementService {

    @Autowired
    AchievementRepository achievementRepository;

    public Achievement save(Achievement achievement){
        return achievementRepository.save(achievement);
    }

    public List<Integer> climbedMountainIds(String userName){
        return achievementRepository.climbedMountainIds(userName);
    }

    public List<Integer> achievementRecords (String userName){
        return achievementRepository.achievementRecords(userName);
    }
}
