package com.example.springsecurityjwt.dto;

import com.example.springsecurityjwt.models.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventWithMemberAndName {
    private Event event;
    private List<MemberWithNameAndPhoto> MemberWithNameAndPhoto;

}
