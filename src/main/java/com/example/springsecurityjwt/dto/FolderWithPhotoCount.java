package com.example.springsecurityjwt.dto;

import com.example.springsecurityjwt.models.PhotoFolder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FolderWithPhotoCount {
    private PhotoFolder folder;
    private int photoCount;
    private String photoPath;
}
