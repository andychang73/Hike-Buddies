package com.example.springsecurityjwt.Repository;

import com.example.springsecurityjwt.models.EventMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface EventMemberRepository extends JpaRepository<EventMember,Integer> {

    @Query(
        value = "SELECT * FROM event_member WHERE event_id =?1 AND user_name = ?2",
        nativeQuery = true
    )
    EventMember checkExistingMember (int eventId, String userName);

    @Query(
            value = "SELECT * FROM event_member WHERE event_id = ?1 AND user_name = ?2 LIMIT 1",
            nativeQuery = true
    )
    EventMember findEventMemberByEventIdAndUserName(int eventId, String userName);

    @Query(
            value = "SELECT * FROM event_member WHERE user_name = ?1 AND status = 0",
            nativeQuery = true
    )
    List<EventMember> findApplicationInProcess(String userName);

    @Query(
            value = "SELECT * FROM event_member WHERE user_name = ?1 AND status = 1",
            nativeQuery = true
    )
    List<EventMember> findAcceptedEvents(String userName);

    @Query(
            value = "SELECT event_id FROM event_member WHERE em_id = ?1",
            nativeQuery = true
    )
    int findEventByEmId(int emId);

    @Query(
            value = "SELECT * FROM event_member WHERE event_id = ?1 AND user_name = ?2",
            nativeQuery =true
    )
    EventMember findEmIdByEventIdAndUserName(int eventId, String userName);

    @Query(
            value = "SELECT * FROM event_member WHERE event_id = ?1 AND status = 0",
            nativeQuery = true
    )
    List<EventMember> findApplicationRequest(int eventId);

    @Query(
            value = "SELECT * FROM event_member WHERE user_name = ?1 AND event_id = ?2",
            nativeQuery = true
    )
    EventMember findEventForVerification(String username, int eventId);

    @Query(
            value = "SELECT * FROM event_member WHERE event_id = ?1 AND status = 1",
            nativeQuery = true
    )
    List<EventMember> findEventMembersByEventId(int eventId);

    @Query(
            value = "SELECT event_id FROM event_member WHERE user_name = ?1 AND status = 1 AND is_applied = 0",
            nativeQuery = true
    )
    List<Integer> findMyEventsToVerify(String userName);

    void deleteByEmId(int emId);

    EventMember findByEmId(int emId);
}
