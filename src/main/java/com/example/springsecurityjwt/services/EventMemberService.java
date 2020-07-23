package com.example.springsecurityjwt.services;

import com.example.springsecurityjwt.Repository.EventMemberRepository;
import com.example.springsecurityjwt.dto.MemberWithName;
import com.example.springsecurityjwt.models.Event;
import com.example.springsecurityjwt.models.EventMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class EventMemberService {

    @Autowired
    private EventMemberRepository membersRepository;

    @Autowired
    MyUserDetailsService userDetailsService;

    public EventMember addMember(EventMember eventMember){
        return membersRepository.save(eventMember);
    }

    public EventMember checkExistingMember(int eventId, String userName){
        return membersRepository.checkExistingMember(eventId, userName);
    }

    public EventMember save(EventMember eventMember){
        return membersRepository.save(eventMember);
    }

    public List<EventMember> findApplicationInProcess(String userName){
        return membersRepository.findApplicationInProcess(userName);
    }

    public List<EventMember> findAcceptedEvents(String userName){
        return membersRepository.findAcceptedEvents(userName);
    }

    public List<EventMember> findApplicationRequest(int eventId){
        return membersRepository.findApplicationRequest(eventId);
    }

    public EventMember findEventForVerification(String userName,int eventId){
        return membersRepository.findEventForVerification(userName, eventId);
    }

    public EventMember findEventMemberByEventIdAndUserName(int eventId, String userName){
        return membersRepository.findEventMemberByEventIdAndUserName(eventId, userName);
    }

    public List<EventMember> findEventMembersByEventId(int eventId){
        return membersRepository.findEventMembersByEventId(eventId);
    }

    public List<MemberWithName> findEventMembersWithNameByEventId(int eventId){
        List<EventMember> members = membersRepository.findEventMembersByEventId(eventId);
        List<MemberWithName> memberWithName = new ArrayList<>();
        members.forEach((EventMember m) ->{
            memberWithName.add(new MemberWithName(m, userDetailsService.getName(m.getUserName())));
        });
        return memberWithName;
    }

    public List<Integer> findMyEventsToVerify (String userName){
        return membersRepository.findMyEventsToVerify(userName);
    }

    public void deleteByEmId(int emId){
        membersRepository.deleteByEmId(emId);
    }

    public EventMember findByEmId(int emId){
        return membersRepository.findByEmId(emId);
    }

    public int findEventByEmId(int emId){
        return membersRepository.findEventByEmId(emId);
    }

    public EventMember findEmIdByEventIdAndUserName(int emId, String userName){
        return membersRepository.findEmIdByEventIdAndUserName(emId, userName);
    }
}
