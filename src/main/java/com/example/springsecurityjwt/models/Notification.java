package com.example.springsecurityjwt.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int notificationId;

    @NotNull
    @NotBlank
    private String userName;

    @NotNull
    @NotBlank
    private String notification;

    @CreationTimestamp
    private Date notifyDate;

    @Column(columnDefinition = "Integer default 0")
    private int status;

    public Notification(String userName, String notification){
        this.userName = userName;
        this.notification = notification;
    }

}
