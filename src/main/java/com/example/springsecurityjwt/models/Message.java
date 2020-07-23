package com.example.springsecurityjwt.models;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int messageId;

    private int eventId;

    @NotNull
    @NotBlank
    private String userName;

    @NotNull
    @NotBlank
    private String msg;

    @CreationTimestamp
    private Date date;

}
