package com.example.springsecurityjwt.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class EventMember {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int emId;

    @NotNull
    @NotBlank
    private String userName;

    private int eventId;

    private int status;

    private String photoPath;

    private int isApplied;

    public EventMember(String userName, int eventId, int status){
        this.userName = userName;
        this.eventId = eventId;
        this.status = status;
    }
}
