package com.example.springsecurityjwt.dto;

import com.example.springsecurityjwt.models.EventMember;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventMemberWithPhoto {
    private EventMember eventMember;
    private String applicantPhoto;
}
