package com.example.springsecurityjwt.models;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.Range;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int eventId;

    @Range(min = 1)
    private int mountainId;

    @NotNull
    @NotBlank
    private String eventName;

    @NotNull
    @NotBlank
    private String leader;

//    @NotNull
//    @NotBlank
    private String leaderName;

//    @NotNull
    private String leaderPhoto;

    @NotNull
    private Date date;
    private int eventType;

    private int minNumOfMember;

    @Range(min = 1)
    private int numOfMember;

    @Column(columnDefinition = "integer default 0")
    private int currentNumOfMember;

    @Column(columnDefinition = "nvarchar(1000)")
    private String description;

    private int status;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    //@OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "eventId", referencedColumnName = "eventId", updatable = false)
    List<Message> messages = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "eventId", referencedColumnName = "eventId", updatable = false)
    List<EventMember> eventMembers = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "eventId", referencedColumnName = "eventId", updatable = false)
    List<Verification> verifications = new ArrayList<>();
}
