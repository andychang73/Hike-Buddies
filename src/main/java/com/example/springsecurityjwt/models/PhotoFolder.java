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
public class PhotoFolder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int folderId;

    @NotNull
    @NotBlank
    private String userName;

    @NotNull
    @NotBlank
    private String folderName;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "folderId", referencedColumnName = "folderId", updatable = false)
    List<Photo> photos = new ArrayList<>();
}
