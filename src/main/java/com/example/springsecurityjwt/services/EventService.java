package com.example.springsecurityjwt.services;

import com.example.springsecurityjwt.Repository.EventMemberRepository;
import com.example.springsecurityjwt.Repository.EventRepository;
import com.example.springsecurityjwt.models.Event;
import com.example.springsecurityjwt.models.EventMember;
import com.example.springsecurityjwt.models.Mountain;
import com.example.springsecurityjwt.util.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    EventMemberRepository eventMemberRepository;

    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    public Event findByEventId(int eventId) {
        return eventRepository.findByEventId(eventId);
    }

    public List<Event> findAllByMountainId(int mountainId) {
        return eventRepository.findAllByMountainId(mountainId);
    }

    public Event updateEvent(Event event) {
        return eventRepository.save(event);
    }

    public List<Event> findMyEvents(String leader) {
        return eventRepository.findMyEvents(leader);
    }

    public void deleteByEventId(int eventId) {
        eventRepository.deleteByEventId(eventId);
    }

    public int findMountainByEventId(int eventId) {
        return eventRepository.findMountainByEventId(eventId);
    }

    public Date findDateByEventId(int eventId) {
        return eventRepository.findDateByEventId(eventId);
    }

    public void addCurrentNumOfMemberByOne(int eventId) {
        eventRepository.addCurrentNumOfMemberByOne(eventId);
    }

    public List<Event> keywordSearch(String keyword) {
        return eventRepository.keywordSearch(keyword);
    }

    public List<Event> findAppliedEvents(String userName) {
        List<EventMember> members = eventMemberRepository.findApplicationInProcess(userName);
        List<Event> events = new ArrayList<>();
        members.stream()
                .forEach((EventMember eventMember) -> {
                    events.add(eventRepository.findByEventId(eventMember.getEventId()));
                });
        return events;
    }

    public List<Event> findAcceptedEvents(String userName) {
        List<EventMember> members = eventMemberRepository.findAcceptedEvents(userName);
        List<Event> events = new ArrayList<>();
        members.stream()
                .forEach((EventMember eventMember) -> {
                    events.add(eventRepository.findByEventId(eventMember.getEventId()));
                });
        return events;
    }

    public List<Event> findMyEventsToVerify(String applicant) {
        List<Integer> eventIdList = eventMemberRepository.findMyEventsToVerify(applicant);
        List<Event> allMyEvents = new ArrayList<>();
        eventIdList.forEach(i -> allMyEvents.add(eventRepository.findByEventId(i)));
        List<Event> eventsToVerify = allMyEvents.stream()
                .filter((Event event) -> event.getDate().before(new Date())
                        || (!event.getLeader().equals(applicant)
                        && event.getCurrentNumOfMember() <= 1))
                .map((Event event) -> {
                    event.setVerifications(null);
                    event.setMessages(null);
                    event.setEventMembers(null);
                    return event;
                })
                .collect(Collectors.toList());
        return eventsToVerify;
    }

    public List<Event> getClosestEvents() {
        return eventRepository.getClosestEvents();
    }

}
