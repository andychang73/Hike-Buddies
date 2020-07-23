package com.example.springsecurityjwt.models;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class User {

    @Id
    @NotBlank
    private String userName;

    @NotNull
    @NotBlank
    private String password;

    private String newPassword;

    @Column(insertable = false)
    private boolean active;

    @Column(insertable = false)
    private String roles;

    @NotNull
    @NotBlank
    private String name;

    private String aboutMySelf;

    private String photoPath;

    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true)
    private int userId;

    @OneToMany
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "userName", referencedColumnName = "userName", updatable = false)
    List<EventMember> eventMembers = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "userName", referencedColumnName = "userName", updatable = false)
    List<Verification> verifications = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "userName", referencedColumnName = "userName", updatable = false)
    List<Achievement> achievements = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "userName", referencedColumnName = "userName", updatable = false)
    List<PhotoFolder> folders = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "userName", referencedColumnName = "userName", updatable = false)
    List<Notification> notifications;

}
