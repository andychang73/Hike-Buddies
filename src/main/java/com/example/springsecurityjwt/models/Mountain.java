package com.example.springsecurityjwt.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Mountain {

    @Id
    private int mountainId;

    @NotNull
    @NotBlank
    private String mountainName;
    private int altitude;
    private double latitude;
    private double longitude;
    private int difficulty;
    private int area;
    private int city;

    @Column(columnDefinition = "Integer default 0")
    private int popularity;

    private int summitReached;

    private String district;
    private String description;
    private String photoPath;

    @OneToMany(targetEntity = Event.class)
    @JoinColumn(name = "mountainId", referencedColumnName = "mountainId", updatable = false)
    private List<Event> events = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "mountainId", referencedColumnName = "mountainId", updatable = false)
    private List<Achievement> achievements = new ArrayList<>();

}
