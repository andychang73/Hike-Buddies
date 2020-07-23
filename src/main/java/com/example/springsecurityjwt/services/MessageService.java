package com.example.springsecurityjwt.services;

import com.example.springsecurityjwt.Repository.MessageRepository;
import com.example.springsecurityjwt.models.Event;
import com.example.springsecurityjwt.models.Message;
import com.example.springsecurityjwt.util.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public Message addMessage(Message message){
        return messageRepository.save(message);
    }

    public List<Message> findAllByEventId(int eventId){
        return messageRepository.findAllByEventId(eventId);
    }

    public void deleteByMessageId(int messageId){
        messageRepository.deleteByMessageId(messageId);
    }

    public Optional<Message> findByMessageId(int messageId){
        return messageRepository.findByMessageId(messageId);
    }
}
