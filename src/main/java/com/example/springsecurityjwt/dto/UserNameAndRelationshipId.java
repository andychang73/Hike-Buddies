package com.example.springsecurityjwt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserNameAndRelationshipId {
    private String userName;
    private int relationshipId;
}
