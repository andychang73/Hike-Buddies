package com.example.springsecurityjwt.dto;

import com.example.springsecurityjwt.models.Relationship;
import com.example.springsecurityjwt.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserWithFriends {
    private User user;
    private List<UserAndRelationship> friends;
}
