package com.example.springsecurityjwt.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Calendar;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int photoId;

    @NotNull
    @NotBlank
    private String photoPath;

    @CreationTimestamp
    private Calendar postDate;

    private int folderId;

    @NotNull
    @NotBlank
    private String userName;

    public Photo(String photoPath, int folderId, String userName){
        this.photoPath = photoPath;
        this.folderId = folderId;
        this.userName = userName;
    }
}
