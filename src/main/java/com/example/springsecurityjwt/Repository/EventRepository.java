package com.example.springsecurityjwt.Repository;

import com.example.springsecurityjwt.models.Event;
import com.example.springsecurityjwt.models.Mountain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {

    List<Event> findAllByMountainId(int mountainId);

    Event findByEventId(int eventId);

    @Query(
            value = "SELECT * FROM event WHERE leader = ?1",
            nativeQuery = true
    )
    List<Event> findMyEvents(String leader);

    @Query(
            value = "SELECT mountain_id FROM event WHERE event_id = ?1",
            nativeQuery = true
    )
    int findMountainByEventId(int eventId);

    @Query(
            value = "SELECT date FROM event WHERE event_id = ?1 LIMIT 1",
            nativeQuery =  true
    )
    Date findDateByEventId(int eventId);

    @Query(
            value = "SELECT * FROM event WHERE event_name LIKE ?1% ",
            nativeQuery = true
    )
    List<Event> keywordSearch(String keyword);

    @Modifying
    @Transactional
    @Query(
            value = "UPDATE event SET current_num_of_member = current_num_of_member + 1 WHERE event_id = ?1",
            nativeQuery = true
    )
    void addCurrentNumOfMemberByOne(int eventId);

    @Query(
            value = "SELECT * FROM event WHERE date > now() + INTERVAL 1 DAY ORDER BY date ASC LIMIT 10",
            nativeQuery = true
    )
    List<Event> getClosestEvents();

    void deleteByEventId(int eventId);
}
