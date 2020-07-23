package com.example.springsecurityjwt.dto;

import com.example.springsecurityjwt.models.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageWithNameAndPhoto {
    private Message message;
    private String name;
    private String photoPath;
}
