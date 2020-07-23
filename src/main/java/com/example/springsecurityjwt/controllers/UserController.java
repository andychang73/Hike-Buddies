package com.example.springsecurityjwt.controllers;

import com.example.springsecurityjwt.dto.UserAndRelationship;
import com.example.springsecurityjwt.dto.UserWithFriends;
import com.example.springsecurityjwt.models.Relationship;
import com.example.springsecurityjwt.models.User;
import com.example.springsecurityjwt.services.MyUserDetailsService;
import com.example.springsecurityjwt.services.RelationshipService;
import com.example.springsecurityjwt.util.FilesUtil;
import com.example.springsecurityjwt.util.JwtUtil;
import com.example.springsecurityjwt.util.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/user")
public class UserController {

    private final String userPath = "user";

    //public static final String vmAddress = "http://35.194.165.190:8080/user/";

    public static final String vmAddress = "http://35.194.165.190:80/user/";

    private final String localAddress = "http://localhost:8080/";

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private RelationshipService relationshipService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private FilesUtil filesUtil;

    @PostMapping("/update")
    public Status update(@RequestBody User user, @RequestHeader("Authorization") String jwt){
        String username = jwtUtil.extractUsername(jwt);
        User existingUser = myUserDetailsService.updateUser(username);
        if(existingUser == null){
            return new Status (400, "User does not exist!");
        }
        if(user.getName() != null && user.getUserName() != ""){
            existingUser.setName(user.getName());
        }
        if(user.getAboutMySelf() != null){
            existingUser.setAboutMySelf(user.getAboutMySelf());
        }
        myUserDetailsService.saveUser(existingUser);
        return new Status(200, existingUser);
    }

    @PostMapping("/updatePassword")
    public Status updatePassword(@RequestBody User user, @RequestHeader("Authorization") String jwt){
        String username = jwtUtil.extractUsername(jwt);
        User existingUser = myUserDetailsService.updateUser(username);
        if(existingUser == null){
            return new Status (400, "User does not exist!");
        }
        if(!user.getPassword().equals(existingUser.getPassword())){
            return new Status(401, "Invalid password");
        }
        existingUser.setPassword(user.getNewPassword());
        myUserDetailsService.saveUser(existingUser);
        existingUser.setPassword("");
        return new Status(200, "Password changed successfully!");
    }

    @GetMapping("/info/{userName}")
    public Status getUserInfo(@PathVariable String userName){
        User user = myUserDetailsService.getUserInfo(userName);
        if(user == null){
            return new Status(403, "Data not found");
        }
        user.setPassword(null);
        user.setEventMembers(null);
        user.setVerifications(null);
        user.setFolders(null);
        user.setRoles(null);
        return new Status(200, user);
    }

    @GetMapping("/visiting/{userName}")
    public Status getInfoByUserName(@PathVariable String userName){
        User user = myUserDetailsService.getUserInfo(userName);
        if(user == null){
            return new Status(403, "Data not found");
        }
        user.setPassword(null);
        user.setEventMembers(null);
        user.setVerifications(null);

        int userId = myUserDetailsService.getUserId(userName);
        List<Relationship> userId1Friends = relationshipService.findUserId1Friends(userId);
        List<Relationship> userId2Friends = relationshipService.findUserId2Friends(userId);
        List<UserAndRelationship> friends = new ArrayList<>();
        userId1Friends.forEach((Relationship r)->{
            User friend = myUserDetailsService.findByUserId(r.getUserId1());
            friend.setPhotoPath(UserController.vmAddress+user.getPhotoPath());
            friend.setVerifications(null);
            friend.setPassword(null);
            friend.setEventMembers(null);
            friend.setAchievements(null);
            friend.setRoles(null);
            friend.setFolders(null);
            friends.add(new UserAndRelationship(friend, r));

        });
        userId2Friends.forEach((Relationship r)->{
            User friend = myUserDetailsService.findByUserId(r.getUserId2());
            friend.setPhotoPath(UserController.vmAddress+user.getPhotoPath());
            friend.setVerifications(null);
            friend.setPassword(null);
            friend.setEventMembers(null);
            friend.setAchievements(null);
            friend.setRoles(null);
            friend.setFolders(null);
            friends.add(new UserAndRelationship(friend, r));
        });
        UserWithFriends info = new UserWithFriends(user, friends);

        return new Status(200, info);
    }

    @PostMapping("/uploadPhoto")
    public Status uploadPhoto(@RequestParam("file") MultipartFile file, @RequestHeader("Authorization") String jwt){
        String username = jwtUtil.extractUsername(jwt);
        User existingUser = myUserDetailsService.findByUsername(username).orElse(null);
        if(existingUser.getPhotoPath() != null){
            if(filesUtil.delete("/"+userPath+"/"+existingUser.getPhotoPath()).getStatus() != 200){
                return new Status(404, "Could not delete file");
            }
        }
        Status status = filesUtil.uploadFile(file, userPath);
        if(status.getStatus() != 200){
            return status;
        }
        existingUser.setPhotoPath(status.getMsg());
        myUserDetailsService.saveUser(existingUser);
        return new Status(200, status.getMsg());
    }

    @GetMapping("/loadPhoto/{userName}")
    public Status loadPhoto(@PathVariable String userName){
        User existingUser = myUserDetailsService.findByUsername(userName).orElse(null);
        String filePath = vmAddress + existingUser.getPhotoPath();
        return new Status (200, filePath);
    }

}
