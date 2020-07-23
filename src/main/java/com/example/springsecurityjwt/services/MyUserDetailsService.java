package com.example.springsecurityjwt.services;

import com.example.springsecurityjwt.Repository.UserRepository;
import com.example.springsecurityjwt.models.MyUserDetails;
import com.example.springsecurityjwt.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUserName(userName);
        user.orElseThrow(()-> new UsernameNotFoundException("Not Found: " + userName));
        return user.map(MyUserDetails::new).get();
    }

    public String getName(String userName){
        return userRepository.getName(userName);
    }

    public String getPhotoPath(String userName){
        return userRepository.getPhotoPath(userName);
    }

    public User getUserInfo(String userName){
          return userRepository.getUserInfo(userName);
    }

    public Optional<User> findByUsername(String userName){
        return userRepository.findByUserName(userName);
    }

    public User saveUser(User user){
        return userRepository.save(user);
    }

    public User updateUser(String userName){
        return userRepository.findByUserName(userName).orElse(null);
    }

    public int getUserId(String userName){
        return userRepository.getUserId(userName);
    }

    public User findByUserId(int userId){
        return userRepository.findByUserId(userId);
    }

    public List<User> findAllUser(){
        return userRepository.findAllUser();
    }

    public String getUserName(int userId){
        return userRepository.getUserName(userId);
    }

}
