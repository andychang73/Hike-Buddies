package com.example.springsecurityjwt.dto;

import com.example.springsecurityjwt.models.EventMember;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberWithName {
    private EventMember member;
    private String name;
}
