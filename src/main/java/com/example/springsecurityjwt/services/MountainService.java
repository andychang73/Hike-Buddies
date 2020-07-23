package com.example.springsecurityjwt.services;

import com.example.springsecurityjwt.Repository.AchievementRepository;
import com.example.springsecurityjwt.Repository.MountainRepository;

import com.example.springsecurityjwt.models.Mountain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class MountainService {

    @Autowired
    private MountainRepository mountainRepository;

    @Autowired
    AchievementRepository achievementRepository;

    @Autowired
    EventService eventService;

    public Mountain findByMountainId(int mountainId){
        return mountainRepository.findByMountainId(mountainId);
    }

    public List<Mountain> findByArea(int area){
        return mountainRepository.findByArea(area);
    }

    public List<Mountain> findByCity(int city){
        return mountainRepository.findByCity(city);
    }

    public List<Mountain> findByAltitude3000To3300(){
        return mountainRepository.findByAltitude3000To3300();
    }

    public List<Mountain> findByAltitude3301To3600(){
        return mountainRepository.findByAltitude3301To3600();
    }

    public List<Mountain> findByAltitudeFrom3600(){
        return mountainRepository.findByAltitudeFrom3600();
    }

    public List<Mountain> findByDifficulty(int difficulty){
        return mountainRepository.findByDifficulty(difficulty);
    }

    public List<Mountain> findByPopularity(){
        return mountainRepository.findByPopularity();
    }

    public String findMountainPhotoPath(int mountainId){
        return mountainRepository.findMountainPhotoPath(mountainId);
    }

    public String findMountainNameById(int mountainId){
        return mountainRepository.findMountainNameById(mountainId);
    }

    public Mountain save(Mountain mountain){
        return mountainRepository.save(mountain);
    }

    public List<Mountain> keywordSearch(String keyword){
        return mountainRepository.keywordSearch(keyword);
    }

    public List<Mountain> climbedMountains(String userName){
        List<Integer> mountainIds = achievementRepository.climbedMountainIds(userName);
        List<Mountain> climbedMountains = new ArrayList<>();
        mountainIds.forEach(n->{
            climbedMountains.add(mountainRepository.findByMountainId(n));
        });
        return climbedMountains;
    }

    public void addPopularityByOne(int mountainId){
        mountainRepository.addPopularityByOne(mountainId);
    }

    public void lessPopularityByOne(int eventId){
        int mountainId = eventService.findMountainByEventId(eventId);
        mountainRepository.lessPopularityByOne(mountainId);
    }

    public String getMountainName(int mountainId){
        return mountainRepository.getMountain(mountainId);
    }

    public void addSummitReachedByOne(int mountainId){
        mountainRepository.addSummitReachedByOne(mountainId);
    }

    public List<Mountain> findMostReached(){
        return mountainRepository.findMostReached();
    }
}
