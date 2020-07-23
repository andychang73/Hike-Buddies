package com.example.springsecurityjwt.dto;

import com.example.springsecurityjwt.models.Relationship;
import com.example.springsecurityjwt.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAndRelationship {
    private User user;
    private Relationship relationship;
}
