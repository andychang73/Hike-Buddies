package com.example.springsecurityjwt.services;

import com.example.springsecurityjwt.Repository.RelationshipRepository;
import com.example.springsecurityjwt.models.Relationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class RelationshipService {

    @Autowired
    RelationshipRepository relationshipRepository;

    public Relationship save(Relationship relationship){
        return relationshipRepository.save(relationship);
    }

    public Relationship findByUserId1AndUserId2 (int relationshipId, int userId1){
        return relationshipRepository.findByUserId1AndUserId2(relationshipId, userId1);
    }

    public void delete(Relationship relationship){
        relationshipRepository.delete(relationship);
    }

    public List<Relationship> findAllInvitations(int receiverId){
        return relationshipRepository.findAllInvitation(receiverId);
    }

    public Relationship findByRelationshipId(int relationshipId){
        return relationshipRepository.findByRelationshipId(relationshipId);
    }

    public List<Relationship> findUserId1Friends(int userId1){
        return relationshipRepository.findUserId1Friends(userId1);
    }

    public List<Relationship> findUserId2Friends(int userId2){
        return relationshipRepository.findUserId2Friends(userId2);
    }

    public List<Integer> findUserId1FriendsId(int userId2){
        return relationshipRepository.findUserId1FriendsId(userId2);
    }

    public List<Integer> findUserId2FriendsId(int userId1){
        return relationshipRepository.findUserId2FriendsId(userId1);
    }

    public List<Relationship> findAllFriends(int userId){
        List<Relationship> friends = relationshipRepository.findUserId1Friends(userId);
        relationshipRepository.findUserId2Friends(userId).forEach((Relationship r)-> friends.add(r));
        return friends;
    }
}
