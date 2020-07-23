package com.example.springsecurityjwt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MountainAndDateAndPhoto {
    private int mountainId;
    private String mountainName;
    private Date date;
    private String photo;
}
