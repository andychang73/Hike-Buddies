package com.example.springsecurityjwt.controllers;

import com.example.springsecurityjwt.dto.EventWithMemberAndName;
import com.example.springsecurityjwt.dto.EventsWithMountainNameAndPhotoPath;
import com.example.springsecurityjwt.dto.MemberWithNameAndPhoto;
import com.example.springsecurityjwt.models.Event;
import com.example.springsecurityjwt.models.EventMember;
import com.example.springsecurityjwt.models.Mountain;
import com.example.springsecurityjwt.services.EventMemberService;
import com.example.springsecurityjwt.services.EventService;
import com.example.springsecurityjwt.services.MountainService;
import com.example.springsecurityjwt.services.MyUserDetailsService;
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
@RequestMapping("/event")
public class EventController {

    private final String cancel = "解散團隊";

    private final String dismiss = "解散了";

    @Autowired
    EventService eventService;

    @Autowired
    EventMemberService emService;

    @Autowired
    MountainService mountainService;

    @Autowired
    MyUserDetailsService userDetailsService;

    @Autowired
    JwtUtil jwtUtil;

    @Transactional
    @PostMapping("/create")
    public Status createEvent(@RequestBody Event event, @RequestHeader("Authorization") String jwt) {
        if(event.getMountainId() < 0 || event.getMountainId() > 105 ||
            event.getMountainId() > 82 && event.getMountainId() < 87)
        if (event.getDate().before(new Date())) {
            return new Status(401, "The create event in the past!");
        }
        if (event.getStatus() != 0 && event.getStatus() != 1) {
            return new Status(400, "Invalid event status!");
        }
        String userName = jwtUtil.extractUsername(jwt);
        event.setLeader(userName);
        event.setLeaderName(userDetailsService.getName(userName));
        event.setLeaderPhoto(UserController.vmAddress + userDetailsService.getPhotoPath(userName));
        event.setCurrentNumOfMember(1);
        Event newEvent = eventService.createEvent(event);
        //create event and assign leader

        emService.addMember(new EventMember(userName, newEvent.getEventId(), 1));
        //instantiate event member and add it to event

        mountainService.addPopularityByOne(event.getMountainId());
        //add popularity to mountain
        return new Status(200, newEvent);
    }

    @GetMapping("/bymountain/{id}")
    public Status findAllByMountainId(@PathVariable int id) {
        if (id < 0 || id > 105 || (id >= 83 && id <= 87)) {
            return new Status(400, "This mountain ID does not exist!");
        }
        List<Event> events = eventService.findAllByMountainId(id);
        if (events.isEmpty()) {
            return new Status(400, "Data not found");
        }
        List<Event> eventsFiltered = events.stream()
                .filter((Event event) -> event.getDate().after(new Date()))
                .map((Event event)->{
                    event.setMessages(null);
                    event.setVerifications(null);
                    return event;
                })
                .sorted(Comparator.comparing(Event::getDate))
                .collect(Collectors.toList());
        return new Status(200, eventsFiltered);
    }

    @Transactional
    @PostMapping("/deleteEvent")
    public Status deleteEvent(@RequestBody Event event, @RequestHeader("Authorization") String jwt) {
        String leader = jwtUtil.extractUsername(jwt);
        Event existingEvent = eventService.findByEventId(event.getEventId());
        if (existingEvent == null || existingEvent.getDate().before(new Date()) || !existingEvent.getLeader().equals(leader)) {
            return new Status(400, "Invalid event or user is not the leader of the event!");
        }
        mountainService.lessPopularityByOne(event.getEventId());
        List<EventMember> members = emService.findEventMembersByEventId(event.getEventId());
        members.forEach((EventMember m)->{
            if(!m.getUserName().equals(leader) && m.getStatus() == 1){
                try {
                    SendGridEmailer.getInstance().sendEmail(cancel,
                            m.getUserName(),
                            existingEvent.getLeaderName()+dismiss+existingEvent.getEventName());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        eventService.deleteByEventId(event.getEventId());
        return new Status(200, "Event deleted");
    }

    @GetMapping("/findByArea/{area}")
    public Status findByArea(@PathVariable int area) {
        if (area < 1 || area > 3) {
            return new Status(400, "Invalid area parameter!");
        }
        List<Mountain> mountainWithEvents = mountainWithEvents(mountainService.findByArea(area));
        if (mountainWithEvents.isEmpty()) {
            return new Status(200, "No events is this area!");
        }
        List<Mountain> eventsFiltered = filterPastEvents(mountainWithEvents);
        return new Status(200, eventsFiltered);
    }

    @GetMapping("/findByCity/{city}")
    public Status findByCity(@PathVariable int city) {
        if (city < 1 || city > 9) {
            return new Status(400, "Invalid city parameter!");
        }
        List<Mountain> mountainsWithEvents = mountainWithEvents(mountainService.findByCity(city));
        if (mountainsWithEvents.isEmpty()) {
            return new Status(401, "No events in this city!");
        }
        List<Mountain> eventsFiltered = filterPastEvents(mountainsWithEvents);
        return new Status(200, eventsFiltered);
    }

    @GetMapping("/findByAltitude3000To3300")
    public Status findByAltitude3000To3300() {
        List<Mountain> mountainWithEvents = mountainWithEvents(mountainService.findByAltitude3000To3300());
        if (mountainWithEvents.isEmpty()) {
            return new Status(400, "No events in this altitude range");
        }
        List<Mountain> eventsFiltered = filterPastEvents(mountainWithEvents);
        return new Status(200, eventsFiltered);
    }

    @GetMapping("/findByAltitude3301To3600")
    public Status findByAltitude3301To3600() {
        List<Mountain> mountainWithEvents = mountainWithEvents(mountainService.findByAltitude3301To3600());
        if (mountainWithEvents.isEmpty()) {
            return new Status(400, "No events in this altitude range");
        }
        List<Mountain> eventsFiltered = filterPastEvents(mountainWithEvents);
        return new Status(200, eventsFiltered);
    }

    @GetMapping("/findByAltitudeFrom3600")
    public Status findByAltitudeFrom3600() {
        List<Mountain> mountainWithEvents = mountainWithEvents(mountainService.findByAltitudeFrom3600());
        if (mountainWithEvents.isEmpty()) {
            return new Status(400, "No events in this altitude range");
        }
        List<Mountain> eventsFiltered = filterPastEvents(mountainWithEvents);
        return new Status(200, eventsFiltered);
    }

    @GetMapping("/findByDifficulty/{difficulty}")
    public Status findByDifficulty(@PathVariable int difficulty) {
        if (difficulty < 1 || difficulty > 3) {
            return new Status(400, "Invalid difficulty parameter!");
        }
        List<Mountain> mountainWithEvents = mountainWithEvents(mountainService.findByDifficulty(difficulty));
        if (mountainWithEvents.isEmpty()) {
            return new Status(401, "No events in this level!");
        }
        List<Mountain> eventsFiltered = filterPastEvents(mountainWithEvents);
        return new Status(200, eventsFiltered);
    }

    @GetMapping("/findByPopularity")
    public Status findByPopularity() {
        List<Mountain> eventsFiltered = filterPastEvents(mountainService.findByPopularity());
        return new Status(200, eventsFiltered);
    }

    @GetMapping("/info/{eventId}")
    public Status findByEventIt(@PathVariable int eventId) {
        Event event = eventService.findByEventId(eventId);
        if (event == null) {
            return new Status(400, "Event does not exist!");
        }
        return new Status(200, event);
    }

    @PostMapping("/changeDate")
    public Status changeDate(@RequestBody Event event) {
        Event existingEvent = eventService.findByEventId(event.getEventId());
        if (existingEvent == null) {
            return new Status(400, "Event does not exist!");
        }
        existingEvent.setDate(event.getDate());
        eventService.updateEvent(existingEvent);
        return new Status(200, existingEvent);
    }

    @GetMapping("/{eventId}")
    public Status getEventById(@PathVariable int eventId){
        Event event = eventService.findByEventId(eventId);
        if(event == null){
            return new Status (400, "Invalid event id!");
        }
        List<MemberWithNameAndPhoto> members = new ArrayList<>();
        event.getEventMembers().forEach((EventMember m)->{
            members.add(new MemberWithNameAndPhoto(m,
                    userDetailsService.getName(m.getUserName()),
                    UserController.vmAddress+userDetailsService.getPhotoPath(m.getUserName())));
        });
        event.setVerifications(null);
        event.setEventMembers(null);
        event.setMessages(null);
        EventWithMemberAndName list = new EventWithMemberAndName(event, members);
        return new Status (200, list);
    }

    @GetMapping("/keyword/{keyword}")
    public Status keywordSearch(@PathVariable String keyword){
        List<Event> events = eventService.keywordSearch(keyword);
        List<EventsWithMountainNameAndPhotoPath> list = new ArrayList<>();
        if(!events.isEmpty()){
            events.forEach((Event e)->{
                if(e.getDate().after(new Date())){
                    e.setMessages(null);
                    e.setVerifications(null);
                    list.add((new EventsWithMountainNameAndPhotoPath(
                            mountainService.findMountainNameById(e.getMountainId()),
                            MountainController.vmAddress+mountainService.findMountainPhotoPath(e.getMountainId()),
                            e
                    )));
                }
            });
        }
        return new Status (200 , list);
    }

    @PostMapping("/updateDescription")
    public Status updateDescription(@RequestHeader("Authorization") String jwt,
                                    @RequestBody Event event){
        String userName = jwtUtil.extractUsername(jwt);
        Event e = eventService.findByEventId(event.getEventId());
        if(e == null || !e.getLeader().equals(userName)){
            return new Status(400, "Event does not exists or user is not the leader!");
        }
        e.setDescription(event.getDescription());
        eventService.updateEvent(e);
        return new Status(200, e);
    }

    @GetMapping("/closest")
    public Status closestEvent(){
        List<Event> events = eventService.getClosestEvents();
        if(events.isEmpty()){
            return new Status(200, "No events!");
        }
        List<EventsWithMountainNameAndPhotoPath> list = new ArrayList<>();
        events.forEach((Event e)->{
            e.setMessages(null);
            e.setVerifications(null);
            list.add((new EventsWithMountainNameAndPhotoPath(
                    mountainService.findMountainNameById(e.getMountainId()),
                    MountainController.vmAddress+mountainService.findMountainPhotoPath(e.getMountainId()),
                    e
                    )));
        });
        return new Status (200, list);
    }

    private List<Mountain> mountainWithEvents(List<Mountain> mountains) {
        return mountains.stream()
                .filter((Mountain mountain) -> !mountain.getEvents().isEmpty())
                .collect(Collectors.toList());
    }

    private List<Mountain> filterPastEvents(List<Mountain> mountains) {
        mountains.forEach((Mountain m) -> {
            m.setPhotoPath(MountainController.vmAddress + m.getPhotoPath());
            m.setAchievements(null);
            List<Event> events = m.getEvents().stream()
                    .filter((Event event) -> event.getDate().after(new Date()))
                    .sorted(Comparator.comparing(Event::getDate))
                    .collect(Collectors.toList());
            events.forEach((Event e) -> {
                e.setMessages(null);
                e.setVerifications(null);
            });
            m.setEvents(events);
        });
        return mountains.stream()
                .filter((Mountain m)-> !m.getEvents().isEmpty())
                .collect(Collectors.toList());
    }
}
