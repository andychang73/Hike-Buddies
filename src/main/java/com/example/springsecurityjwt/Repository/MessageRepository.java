package com.example.springsecurityjwt.Repository;

import com.example.springsecurityjwt.models.Event;
import com.example.springsecurityjwt.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

    List<Message> findAllByEventId(int eventId);

    Optional<Message> findByMessageId(int messageId);

    void deleteByMessageId(int messageId);
}
