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
public class Verification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int verId;
    
    private int eventId;

    @NotNull
    @NotBlank
    private String userName;

    @NotNull
    @NotBlank
    private String verifier;

    @Column(columnDefinition = "Integer default 0")
    private int status;

    @NotNull
    @NotBlank
    private String photoPath;

    public Verification(int eventId, String userName, String verifier, String photoPath){
        this.eventId = eventId;
        this.userName = userName;
        this.verifier = verifier;
        this.photoPath = photoPath;
    }
}
