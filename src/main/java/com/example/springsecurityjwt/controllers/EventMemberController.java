package com.example.springsecurityjwt.controllers;

import com.example.springsecurityjwt.dto.EventMemberWithPhoto;
import com.example.springsecurityjwt.dto.EventsWithMountainNameAndPhotoPath;
import com.example.springsecurityjwt.models.Event;
import com.example.springsecurityjwt.models.EventMember;
import com.example.springsecurityjwt.services.*;
import com.example.springsecurityjwt.util.JwtUtil;
import com.example.springsecurityjwt.util.SendGridEmailer;
import com.example.springsecurityjwt.util.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/eventMember")
public class EventMemberController {

    private String application = "申請加入您的";

    private String joined = "已加入您的";

    private String approved = "核准您進入";

    private String quitEvent = "團員退出團隊";

    private String quit = "退出了";

    @Autowired
    EventMemberService emService;

    @Autowired
    EventService eventService;

    @Autowired
    MyUserDetailsService userDetailsService;

    @Autowired
    MountainService mountainService;

    @Autowired
    JwtUtil jwtUtil;

    @PostMapping("/add")
    public Status addEventMember(@RequestBody EventMember em, @RequestHeader("Authorization") String jwt) throws IOException {
        Event existingEvent = eventService.findByEventId(em.getEventId());
        if (existingEvent == null || existingEvent.getDate().before(new Date()) ||
                existingEvent.getCurrentNumOfMember() >= existingEvent.getNumOfMember()) {
            return new Status(400, "Invalid event or the group is full!");
        }
        String userName = jwtUtil.extractUsername(jwt);
        if (existingEvent.getStatus() == 0) {
            EventMember existingEM = emService.checkExistingMember(em.getEventId(), userName);
            if (existingEM != null) {
                return new Status(402, "You are already in the group!");
            }
        }
        if (existingEvent.getStatus() == 1) {
            EventMember existingEM = emService.checkExistingMember(em.getEventId(), userName);
            if (existingEM != null) {
                switch (existingEM.getStatus()) {
                    case 0:
                        return new Status(404, "You have already applied!");
                    case 1:
                        return new Status(402, "You are already in the group!");
                    case 2:
                        return new Status(405, "You have been declined!");
                }
            }
            EventMember newEM = add(userName, em.getEventId(), 0, existingEvent, existingEvent.getStatus());
            SendGridEmailer.getInstance().sendEmail(application,
                    existingEvent.getLeader(),
                    userDetailsService.getName(userName) + application + existingEvent.getEventName());
            return new Status(200, newEM);
        }
        EventMember newEM = add(userName, em.getEventId(), 1, existingEvent, existingEvent.getStatus());
        SendGridEmailer.getInstance().sendEmail(application,
                existingEvent.getLeader(),
                userDetailsService.getName(userName) + joined + existingEvent.getEventName());
        return new Status(200, newEM);
    }

    @PostMapping("/updateStatus")
    public Status updateStatus(@RequestBody EventMember em) throws IOException {
        if (em.getStatus() != 1 && em.getStatus() != 2) {
            return new Status(406, "Invalid status parameter!");
        }
        EventMember eventMember = emService.findByEmId(em.getEmId());
        if (eventMember == null || eventMember.getStatus() != 0) {
            return new Status(402, "Invalid event member or has status has already been updated!");
        }
        int eventId = emService.findEventByEmId(em.getEmId());
        Event existingEvent = eventService.findByEventId(eventId);
        if(existingEvent.getLeader().equals(eventMember.getUserName())){
            return new Status (401, "Leader is already in the group!");
        }
        if (existingEvent == null || existingEvent.getCurrentNumOfMember() >= existingEvent.getNumOfMember()) {
            return new Status(400, "Invalid event or the group is full");
        }
        if (em.getStatus() == 1) {
            existingEvent.setCurrentNumOfMember(existingEvent.getCurrentNumOfMember() + 1);
            SendGridEmailer.getInstance().sendEmail(approved,
                    eventMember.getUserName(),
                    userDetailsService.getName(existingEvent.getLeader()) +
                            approved + existingEvent.getEventName());
        }        eventMember.setStatus(em.getStatus());
        emService.save(eventMember);
        return new Status(200, eventMember);
    }

    @GetMapping("/appInProcess")
    public Status findApplicationInProcess(@RequestHeader("Authorization") String jwt) {
        String userName = jwtUtil.extractUsername(jwt);
        List<Event> appliedEvents = eventService.findAppliedEvents(userName);
        if (appliedEvents.isEmpty()) {
            return new Status(400, "No application in Process!");
        }
        List<Event> onGoingEvents = appliedEvents.stream()
                .filter((Event event) -> event.getDate().after(new Date()))
                .map((Event event) -> {
                    List<EventMember> members = event.getEventMembers().stream()
                            .filter((EventMember m) -> m.getStatus() == 1)
                            .collect(Collectors.toList());
                    event.setEventMembers(members);
                    return event;
                })
                .collect(Collectors.toList());
        if (onGoingEvents.isEmpty()) {
            return new Status(401, "No application in process!");
        }
        List<EventsWithMountainNameAndPhotoPath> list = putDataTogether(onGoingEvents);
        return new Status(200, list);
    }

    @GetMapping("/appRequest/{eventId}")
    public Status findApplicationRequest(@RequestHeader("Authorization") String jwt, @PathVariable int eventId) {
        String userName = jwtUtil.extractUsername(jwt);
        Event existingEvent = eventService.findByEventId(eventId);
        if (existingEvent == null || existingEvent.getDate().before(new Date()) || !existingEvent.getLeader().equals(userName)) {
            return new Status(400, "Invalid event or user is not the leader of the event");
        }
        List<EventMember> eventMembers = emService.findApplicationRequest(eventId);
        if (eventMembers.isEmpty()) {
            return new Status(200, "No application requests!");
        }
        List<EventMemberWithPhoto> list = new ArrayList<>();
        eventMembers
                .forEach((EventMember eventMember) -> {
                    String applicantPhoto = userDetailsService.getUserInfo(eventMember.getUserName()).getPhotoPath();
                    list.add(new EventMemberWithPhoto(eventMember, UserController.vmAddress + applicantPhoto));
                });
        return new Status(200, list);
    }

    @Transactional
    @PostMapping("/deleteAppInProcess")
    public Status deleteAppInProcess(@RequestBody Event event, @RequestHeader("Authorization") String jwt) {
        String userName = jwtUtil.extractUsername(jwt);
        List<EventMember> members = emService.findApplicationInProcess(userName);
        if (members.isEmpty()) {
            return new Status(400, "No application in process!");
        }
        int emId;
        for (EventMember member : members) {
            if (member.getEventId() == event.getEventId() &&
                    member.getUserName().equals(userName)) {
                emId = member.getEmId();
                emService.deleteByEmId(emId);
                return new Status(200, "Deletion completed!");
            }
        }
        return new Status(400, "Application does not exists!");
    }

    @GetMapping("/acceptedOnGoingEvents")
    public Status findAcceptedOnGoingEvents(@RequestHeader("Authorization") String jwt) {
        String userName = jwtUtil.extractUsername(jwt);
        List<Event> acceptedEvents = eventService.findAcceptedEvents(userName);
        if (acceptedEvents.isEmpty()) {
            return new Status(200, "No accepted events!");
        }
        List<Event> onGoingEvents = acceptedEvents.stream()
                .filter((Event event) -> event.getDate().after(new Date()))
                .map((Event event) -> {
                    List<EventMember> members = event.getEventMembers().stream()
                            .filter((EventMember m) -> m.getStatus() == 1)
                            .collect(Collectors.toList());
                    event.setEventMembers(members);
                    return event;
                })
                .sorted(Comparator.comparing(Event::getDate))
                .collect(Collectors.toList());
        if (onGoingEvents.isEmpty()) {
            return new Status(200, "No accepted on going events!");
        }
        List<EventsWithMountainNameAndPhotoPath> list = putDataTogether(onGoingEvents);
        return new Status(200, list);
    }

    @GetMapping("/acceptedHistoryEvents")
    public Status findAcceptedHistoryEvents(@RequestHeader("Authorization") String jwt) {
        String userName = jwtUtil.extractUsername(jwt);
        List<Event> acceptedEvents = eventService.findAcceptedEvents(userName);
        if (acceptedEvents.isEmpty()) {
            return new Status(200, "No accepted events!");
        }
        List<Event> historyEvents = acceptedEvents.stream()
                .filter((Event event) -> event.getDate().before(new Date()))
                .map((Event event) -> {
                    List<EventMember> members = event.getEventMembers().stream()
                            .filter((EventMember m) -> m.getStatus() == 1)
                            .collect(Collectors.toList());
                    event.setEventMembers(members);
                    return event;
                })
                .collect(Collectors.toList());
        if (historyEvents.isEmpty()) {
            return new Status(201, "No history events!");
        }
        List<EventsWithMountainNameAndPhotoPath> list = putDataTogether(historyEvents);
        return new Status(200, list);
    }

    @Transactional
    @PostMapping("/quitEvent")
    public Status quitEvent(@RequestBody EventMember em, @RequestHeader("Authorization") String jwt) throws IOException {
        String userName = jwtUtil.extractUsername(jwt);
        Event event = eventService.findByEventId(em.getEventId());
        if (event == null || event.getDate().before(new Date()) || event.getLeader().equals(userName)) {
            return new Status(400, "Invalid event or user himself is the leader cannot quit his/her event");
        }
        EventMember eventMember = emService.findEmIdByEventIdAndUserName(em.getEventId(), userName);
        if (eventMember == null || eventMember.getStatus() != 1) {
            return new Status(402, "Invalid event member");
        }
        emService.deleteByEmId(eventMember.getEmId());
        event.setCurrentNumOfMember(event.getCurrentNumOfMember() - 1);
        eventService.updateEvent(event);
        SendGridEmailer.getInstance().sendEmail(quitEvent,
                event.getLeader(),
                userDetailsService.getName(userName) + quit + event.getEventName());
        return new Status(200, "Quit successfully!");
    }

    @PostMapping("/members")
    public Status findEventMembersByEventId(@RequestBody Event event, @RequestHeader("Authorization") String jwt) {
        Event existingEvent = eventService.findByEventId(event.getEventId());
        if (existingEvent == null || existingEvent.getDate().after(new Date())) {
            return new Status(400, "Event does not exist or has not yet happened!");
        }
        List<EventMember> eventMembers = emService.findEventMembersByEventId(existingEvent.getEventId());
        if (eventMembers.isEmpty()) {
            return new Status(401, "No event members");
        }
        String userName = jwtUtil.extractUsername(jwt);
        for (int i = 0; i < eventMembers.size(); i++) {
            if (eventMembers.get(i).getUserName().equals(userName)) {
                List<EventMember> listWithOutApplicant = eventMembers.stream()
                        .filter((EventMember eventMember) -> !eventMember.getUserName().equals(userName))
                        .collect(Collectors.toList());
                return new Status(200, listWithOutApplicant);
            }
        }
        return new Status(402, "Applicant was not in this event!");
    }

    public EventMember add(String userName, int eventId, int status, Event existingEvent, int eventStatus) {
        EventMember newEM = emService.addMember(new EventMember(userName, eventId, status));
        if (newEM == null) {
            return null;
        }
        if (eventStatus == 0) {
            existingEvent.setCurrentNumOfMember(existingEvent.getCurrentNumOfMember() + 1);
            eventService.updateEvent(existingEvent);
        }
        return newEM;
    }

    private List<EventsWithMountainNameAndPhotoPath> putDataTogether(List<Event> events) {
        List<EventsWithMountainNameAndPhotoPath> list = new ArrayList<>();
        events.forEach((Event event) -> {
            event.setVerifications(null);
            event.setMessages(null);
            String mountainName = mountainService.findMountainNameById(event.getMountainId());
            String photoPath = MountainController.vmAddress +
                    mountainService.findMountainPhotoPath(event.getMountainId());
            list.add(new EventsWithMountainNameAndPhotoPath(mountainName, photoPath, event));
        });
        return list;
    }
}
