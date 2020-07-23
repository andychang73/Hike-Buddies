package com.example.springsecurityjwt.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"userId1", "userId2"})
})
public class Relationship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int relationshipId;

    private int userId1;

    private int userId2;

    private int status;

    private int actionUserId;

    public Relationship(int userId1, int userId2, int status, int actionUserId) {
        this.userId1 = userId1;
        this.userId2 = userId2;
        this.status = status;
        this.actionUserId = actionUserId;
    }
}
