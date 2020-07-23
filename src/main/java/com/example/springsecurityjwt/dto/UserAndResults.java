package com.example.springsecurityjwt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAndResults {
    private String userName;
    private String name;
    private String photoPath;
    private int numOfMountainClimbed;
    private int totalNumOfRecords;
    private int totalAltitude;
}
