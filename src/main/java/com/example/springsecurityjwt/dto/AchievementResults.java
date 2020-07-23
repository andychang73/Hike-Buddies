package com.example.springsecurityjwt.dto;

import com.example.springsecurityjwt.models.Mountain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AchievementResults {
    private int numOfMountainsClimbed;
    private int totalNumOfRecords;
    private int totalAltitude;
    private List<Mountain> climbedMountain;
}
