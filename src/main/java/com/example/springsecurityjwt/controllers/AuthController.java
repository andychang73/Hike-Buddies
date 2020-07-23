package com.example.springsecurityjwt.controllers;

import com.example.springsecurityjwt.util.AuthenticationRequest;
import com.example.springsecurityjwt.util.AuthenticationResponse;
import com.example.springsecurityjwt.models.User;
import com.example.springsecurityjwt.services.MyUserDetailsService;
import com.example.springsecurityjwt.util.Status;
import com.example.springsecurityjwt.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping(path = "/login")
    public Status createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            return new Status(401, "Invalid user name or password");
        }
        final UserDetails userDetails = myUserDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());

        final String jwt = jwtTokenUtil.generateToken(userDetails);
        AuthenticationResponse response = new AuthenticationResponse(jwt);
        return new Status(200, response);
    }

    @PostMapping(path = "/register")
    public Status register(@RequestBody User user) {
        Optional<User> checkUserName = myUserDetailsService.findByUsername(user.getUserName());
        if (!checkUserName.isEmpty()) {
            return new Status(404, "Account has been registered!");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        myUserDetailsService.saveUser(user);
        user.setPassword(null);
        return new Status(200, user);
    }


}
