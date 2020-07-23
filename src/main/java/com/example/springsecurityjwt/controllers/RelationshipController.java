package com.example.springsecurityjwt.controllers;

import com.example.springsecurityjwt.dto.InvitationWithUserDetails;
import com.example.springsecurityjwt.dto.UserAndRelationship;
import com.example.springsecurityjwt.dto.UserNameAndRelationshipId;
import com.example.springsecurityjwt.models.Relationship;
import com.example.springsecurityjwt.models.User;
import com.example.springsecurityjwt.services.MyUserDetailsService;
import com.example.springsecurityjwt.services.NotificationService;
import com.example.springsecurityjwt.services.RelationshipService;
import com.example.springsecurityjwt.util.JwtUtil;
import com.example.springsecurityjwt.util.SendGridEmailer;
import com.example.springsecurityjwt.util.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/relationship")
public class RelationshipController {

    private final int pending = 0;

    @Autowired
    RelationshipService relationshipService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    MyUserDetailsService userDetailsService;

    @Autowired
    JwtUtil jwtUtil;

    @PostMapping("/add")
    public Status addAsFriend(@RequestHeader("Authorization") String jwt,
                              @RequestBody User user) throws IOException {

        String userName = jwtUtil.extractUsername(jwt);
        int inviter = userDetailsService.getUserId(userName);
        int receiver = userDetailsService.getUserId(user.getUserName());
        if (receiver == 0 || receiver == inviter) {
            return new Status(400, "Target user does not exist or cannot invite user him/her self!");
        }
        int userId1 = 0;
        int userId2 = 0;
        if (inviter < receiver) {
            userId1 = inviter;
            userId2 = receiver;
        } else {
            userId1 = receiver;
            userId2 = inviter;
        }
        Relationship existingRelationship = relationshipService.findByUserId1AndUserId2(userId1, userId2);
        if(existingRelationship != null){
            existingRelationship.setStatus(pending);
            existingRelationship.setActionUserId(inviter);
            relationshipService.save(existingRelationship);
            return new Status (200, existingRelationship);
        }
        try {
            Relationship relationship = new Relationship(userId1, userId2, pending, inviter);
            relationshipService.save(relationship);
            return new Status(200, relationship);
        } catch (Exception e) {
            return new Status(401, e.getMessage());
        }
    }

    @PostMapping("/cancelInvitation")
    public Status cancelInvitation(@RequestHeader("Authorization") String jwt,
                                   @RequestBody User user) {
        String userName = jwtUtil.extractUsername(jwt);
        int userId1 = userDetailsService.getUserId(userName);
        int userId2 = userDetailsService.getUserId(user.getUserName());
        if(userId1 > userId2){
            int tmp = userId1;
            userId1 = userId2;
            userId2 = tmp;
        }
        Relationship r = relationshipService.findByUserId1AndUserId2(userId1, userId2);
        if (r == null || r.getStatus() != 0) {
            return new Status(400, "Invalid relationship!");
        }
        relationshipService.delete(r);
        return new Status(200, "Invitation cancelled!");
    }

    @GetMapping("/getInvitations")
    public Status getInvitations(@RequestHeader("Authorization") String jwt) {
        String userName = jwtUtil.extractUsername(jwt);
        int receiver = userDetailsService.getUserId(userName);
        List<Relationship> possibleInvitations = relationshipService.findAllInvitations(receiver);
        if (possibleInvitations.isEmpty()) {
            return new Status(400, "No Invitations!");
        }
        List<Relationship> invitations = possibleInvitations.stream()
                .filter((Relationship r) -> r.getStatus() == 0 && r.getActionUserId() != receiver)
                .collect(Collectors.toList());
        if (invitations.isEmpty()) {
            return new Status(401, "No Invitations!");
        }
        List<InvitationWithUserDetails> inviter = new ArrayList<>();
        invitations.forEach((Relationship r)->{
            User user = userDetailsService.findByUserId(r.getActionUserId());
            inviter.add(new InvitationWithUserDetails(r,user));
        });
        inviter.forEach((InvitationWithUserDetails i)->{
            i.getUser().setPhotoPath(UserController.vmAddress+i.getUser().getPhotoPath());
            i.getUser().setRoles(null);
            i.getUser().setPassword(null);
            i.getUser().setNotifications(null);
            i.getUser().setFolders(null);
            i.getUser().setAchievements(null);
            i.getUser().setEventMembers(null);
            i.getUser().setVerifications(null);
        });
        return new Status(200, inviter);
    }

    @GetMapping("/sentInvitation")
    public Status getInvitationList(@RequestHeader("Authorization") String jwt){
        String userName = jwtUtil.extractUsername(jwt);
        int inviter = userDetailsService.getUserId(userName);
        List<UserNameAndRelationshipId> invitedFriends = new ArrayList<>();
        List<Integer> userId1Friends = relationshipService.findUserId1FriendsId(inviter);
        userId1Friends.forEach((Integer i)->{
            invitedFriends.add(new UserNameAndRelationshipId(userDetailsService.getUserName(i),i));
        });
        List<Integer> userId2Friends = relationshipService.findUserId2FriendsId(inviter);
        userId2Friends.forEach((Integer i)->{
            invitedFriends.add(new UserNameAndRelationshipId(userDetailsService.getUserName(i),i));
        });
        return new Status (200, invitedFriends);
    }

    @PostMapping("/update")
    public Status updateStatus(@RequestHeader("Authorization") String jwt,
                               @RequestBody Relationship relationship){
        if(relationship.getStatus() < 1 || relationship.getStatus() > 3){
            return new Status (400, "Invalid status parameter!");
        }
        String userName = jwtUtil.extractUsername(jwt);
        int actionUser = userDetailsService.getUserId(userName);
        Relationship r = relationshipService.findByRelationshipId(relationship.getRelationshipId());
        if(r == null || (r.getUserId1() != actionUser && r.getUserId2() != actionUser)){
            return new Status(401, "Invalid relationship!");
        }
        r.setStatus(relationship.getStatus());
        r.setActionUserId(actionUser);
        relationshipService.save(r);
        return new Status (200, r);
    }

    @GetMapping("/friends/{userName}")
    public Status getMyFriends(@PathVariable String userName){
        int userId = userDetailsService.getUserId(userName);
        List<Relationship> userId1Friends = relationshipService.findUserId1Friends(userId);
        List<Relationship> userId2Friends = relationshipService.findUserId2Friends(userId);
        if(userId1Friends.isEmpty() && userId2Friends.isEmpty()){
            return new Status (200, "No friends!");
        }
        List<UserAndRelationship> friends = new ArrayList<>();
        userId1Friends.forEach((Relationship r)->{
            User user = userDetailsService.findByUserId(r.getUserId1());
            user.setPhotoPath(UserController.vmAddress+user.getPhotoPath());
            user.setVerifications(null);
            user.setPassword(null);
            user.setEventMembers(null);
            user.setAchievements(null);
            user.setRoles(null);
            user.setFolders(null);
            friends.add(new UserAndRelationship(user, r));

        });
        userId2Friends.forEach((Relationship r)->{
            User user = userDetailsService.findByUserId(r.getUserId2());
            user.setPhotoPath(UserController.vmAddress+user.getPhotoPath());
            user.setVerifications(null);
            user.setPassword(null);
            user.setEventMembers(null);
            user.setAchievements(null);
            user.setRoles(null);
            user.setFolders(null);
            friends.add(new UserAndRelationship(user, r));
        });
        return new Status (200, friends);
    }
}
