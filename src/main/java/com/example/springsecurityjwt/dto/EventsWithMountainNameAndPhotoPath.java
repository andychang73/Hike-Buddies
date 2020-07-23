package com.example.springsecurityjwt.dto;

import com.example.springsecurityjwt.models.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventsWithMountainNameAndPhotoPath {
    private String mountainName;
    private String photoPath;
    private Event event;
}
