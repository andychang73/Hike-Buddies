package com.example.springsecurityjwt.Repository;

import com.example.springsecurityjwt.models.Verification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface VerificationRepository extends JpaRepository<Verification, Integer> {

    Optional<Verification> findVerificationByVerId(int verId);

    @Query(
            value = "SELECT * FROM verification WHERE event_id = ?1 AND user_name = ?2",
            nativeQuery = true
    )
    List<Verification> findVerificationByEventIdAndUserName(int eventId, String userName);

    @Query(
            value = "SELECT * FROM verification WHERE verifier = ?1 AND status = 0",
            nativeQuery = true
    )
    List<Verification> findRequestByVerifier(String verifier);

    @Query(
            value = "SELECT * FROM verification WHERE event_id = ?1 AND user_name = ?2 AND status = 1 LIMIT 1",
            nativeQuery = true
    )
    Verification checkVerified(int eventId, String userName);

    @Query(
            value = "SELECt * FROM verification WHERE user_name = ?1 AND status = 1",
            nativeQuery = true
    )
    List<Verification> findAllVerifiedByUserName(String userName);
}
