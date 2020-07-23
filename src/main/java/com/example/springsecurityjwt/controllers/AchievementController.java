package com.example.springsecurityjwt.controllers;

import com.example.springsecurityjwt.dto.AchievementResults;
import com.example.springsecurityjwt.dto.MountainAndDateAndPhoto;
import com.example.springsecurityjwt.dto.UserAndResults;
import com.example.springsecurityjwt.models.*;
import com.example.springsecurityjwt.services.*;
import com.example.springsecurityjwt.util.JwtUtil;
import com.example.springsecurityjwt.util.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/achievement")
public class AchievementController {

    @Autowired
    AchievementService achievementService;

    @Autowired
    MountainService mountainService;

    @Autowired
    VerificationService verificationService;

    @Autowired
    EventService eventService;

    @Autowired
    MyUserDetailsService userDetailsService;

    @Autowired
    JwtUtil jwtUtil;


    @GetMapping("/{userName}")
    public Status getResults(@PathVariable String userName){
        AchievementResults results = results(userName);
        return new Status (200, results);
    }

    @GetMapping("/eventAndPhoto/{userName}")
    public Status eventAndPhoto(@PathVariable String userName){
        List<Verification> verifications = verificationService.findAllVerifiedByUserName(userName);
        List<MountainAndDateAndPhoto> list = new ArrayList<>();
        verifications.forEach((Verification v)->{
            Event event = eventService.findByEventId(v.getEventId());
            list.add(new MountainAndDateAndPhoto(event.getMountainId(),
                    mountainService.getMountainName(event.getMountainId()),
                    event.getDate(),
                    VerificationController.vmAddress+v.getPhotoPath()));
        });
        return new Status (200, list);
    }

    @GetMapping("/mostPopularByAltitude")
    public Status mostPopularByAltitude(){
        List<UserAndResults> beforeSorting = userAndResults();
                List<UserAndResults> afterSorting = beforeSorting.stream()
                .sorted(Comparator.comparing(UserAndResults::getTotalAltitude).reversed())
                .collect(Collectors.toList());
        return new Status (200, afterSorting);
    }

    @GetMapping("/mostPopularByRecords")
    public Status mostPopularByRecords(){
        List<UserAndResults> beforeSorting = userAndResults();
        List<UserAndResults> afterSorting = beforeSorting.stream()
                .sorted(Comparator.comparing(UserAndResults::getTotalNumOfRecords).reversed())
                .collect(Collectors.toList());
        return new Status (200, afterSorting);
    }

    public List<UserAndResults> userAndResults(){
        List<User> users = userDetailsService.findAllUser();
        List<UserAndResults> userAndResults = new ArrayList<>();
        for(User user: users){
            AchievementResults results = results(user.getUserName());
            results.setClimbedMountain(null);
            userAndResults.add(new UserAndResults(user.getUserName(),
                    user.getName(),
                    UserController.vmAddress+user.getPhotoPath(),
                    results.getNumOfMountainsClimbed(),
                    results.getTotalNumOfRecords(),
                    results.getTotalAltitude()));
        }
        return userAndResults;
    }

    public AchievementResults results(String userName){
        List<Mountain> climbedMountains = mountainService.climbedMountains(userName);
        climbedMountains.forEach(n->{
            n.setDescription(null);
            n.setEvents(null);
            n.setAchievements(null);
        });
        List<Integer> achievementRecords = achievementService.achievementRecords(userName);
        int numOfMountainsClimbed = climbedMountains.size();
        int totalNumOfRecords = achievementRecords.size();
        int totalAltitude = 0;
        for(int i = 0 ; i < achievementRecords.size(); i++){
            totalAltitude += mountainService.findByMountainId(achievementRecords.get(i)).getAltitude();
        }
        return new AchievementResults(numOfMountainsClimbed, totalNumOfRecords, totalAltitude, climbedMountains);
    }
}
