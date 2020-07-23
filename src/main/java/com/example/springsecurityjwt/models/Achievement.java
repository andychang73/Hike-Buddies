package com.example.springsecurityjwt.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int achievementId;

    private int mountainId;

    @NotNull
    @NotBlank
    private String userName;

    public Achievement(int mountainId, String userName){
        this.mountainId = mountainId;
        this.userName = userName;
    }
}
