package com.example.springsecurityjwt.dto;

import com.example.springsecurityjwt.models.Verification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerificationWithDetails {
    private Verification verification;
    private String name;
    private String userPhoto;
    private Date date;
    private String mountainName;
}
