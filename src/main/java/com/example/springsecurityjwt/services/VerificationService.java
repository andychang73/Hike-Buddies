package com.example.springsecurityjwt.services;

import com.example.springsecurityjwt.Repository.VerificationRepository;
import com.example.springsecurityjwt.models.Verification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VerificationService {

    @Autowired
    VerificationRepository verificationRepository;

    public Verification save(Verification verification){
        return verificationRepository.save(verification);
    }

    public List<Verification> findVerificationByEventIdAndUserName(int eventId, String userName){
        return verificationRepository.findVerificationByEventIdAndUserName(eventId, userName);
    }

    public List<Verification> findRequestByVerifier(String verifier){
        return verificationRepository.findRequestByVerifier(verifier);
    }

    public Optional<Verification> findVerificationByVerId(int verId){
        return verificationRepository.findVerificationByVerId(verId);
    }

    public List<Verification> findAllVerifiedByUserName(String userName){
        return verificationRepository. findAllVerifiedByUserName(userName);
    }
}
