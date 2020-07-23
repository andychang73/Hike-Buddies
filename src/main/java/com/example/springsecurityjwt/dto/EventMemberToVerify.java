package com.example.springsecurityjwt.dto;

import com.example.springsecurityjwt.models.Event;
import com.example.springsecurityjwt.models.EventMember;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventMemberToVerify {
    private Event event;
    private List<MemberWithName> members;
}
