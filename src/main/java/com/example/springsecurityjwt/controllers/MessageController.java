package com.example.springsecurityjwt.controllers;

import com.example.springsecurityjwt.dto.MessageWithNameAndPhoto;
import com.example.springsecurityjwt.models.Event;
import com.example.springsecurityjwt.models.Message;
import com.example.springsecurityjwt.services.EventService;
import com.example.springsecurityjwt.services.MessageService;
import com.example.springsecurityjwt.services.MyUserDetailsService;
import com.example.springsecurityjwt.util.JwtUtil;
import com.example.springsecurityjwt.util.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    MyUserDetailsService userDetailsService;

    @Autowired
    EventService eventService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/add")
    public Status addMessage(@RequestBody Message message, @RequestHeader("Authorization") String jwt) {
        String userName = jwtUtil.extractUsername(jwt);
        Event event = eventService.findByEventId(message.getEventId());
        if(event == null){
            return new Status (400, "Event does not exist!");
        }
        message.setUserName(userName);
        Message msg = messageService.addMessage(message);
        if (msg == null) {
            return new Status(404, "Data not found");
        }
        return new Status(200, msg);
    }

    @GetMapping("/getByEvent/{id}")
    public Status findAllByEventId(@PathVariable int id) {
        List<Message> messages = messageService.findAllByEventId(id);
        if (messages.isEmpty()) {
            return new Status(403, "Data not found");
        }
        List<MessageWithNameAndPhoto> msg = new ArrayList<>();
        messages.forEach((Message m)->{
            String photoPath = UserController.vmAddress + userDetailsService.getPhotoPath(m.getUserName());
            msg.add(new MessageWithNameAndPhoto(m,
                    userDetailsService.getName(m.getUserName()),
                    photoPath));
        });
        return new Status(200, msg);
    }

    @Transactional
    @PostMapping("/delete")
    public Status deleteByMessageId(@RequestHeader("Authorization") String jwt,
                                    @RequestBody Message message){
        String userName = jwtUtil.extractUsername(jwt);
        Optional<Message> msg = messageService.findByMessageId(message.getMessageId());
        if(msg.isEmpty() || !msg.get().getUserName().equals(userName)){
            return new Status (400, "Invalid message!");
        }
        messageService.deleteByMessageId(message.getMessageId());
        return new Status (200, "Message deleted!");
    }
}
