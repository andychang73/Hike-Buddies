package com.example.springsecurityjwt.controllers;

import com.example.springsecurityjwt.dto.EventMemberToVerify;
import com.example.springsecurityjwt.dto.EventVerification;
import com.example.springsecurityjwt.dto.MemberWithName;
import com.example.springsecurityjwt.dto.VerificationWithDetails;
import com.example.springsecurityjwt.models.*;
import com.example.springsecurityjwt.services.*;
import com.example.springsecurityjwt.util.FilesUtil;
import com.example.springsecurityjwt.util.JwtUtil;
import com.example.springsecurityjwt.util.SendGridEmailer;
import com.example.springsecurityjwt.util.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/verification")
public class VerificationController {

    private final String verificationPath = "verification";

    public static final String vmAddress = "http://35.194.165.190:80/verification/";

    private final String subject = "足跡驗證";

    private final String application = "向您申請足跡驗證";

    private final String approved = "驗證了您的足跡";

    @Autowired
    VerificationService verificationService;

    @Autowired
    EventMemberService emService;

    @Autowired
    EventService eventService;

    @Autowired
    AchievementService achievementService;

    @Autowired
    MyUserDetailsService userDetailsService;

    @Autowired
    MountainService mountainService;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    FilesUtil filesUtil;


    @PostMapping("/apply")
    public Status applyVerification(@RequestHeader("Authorization") String jwt, @RequestBody EventVerification ev) throws IOException {
        Event historyEvent = eventService.findByEventId(ev.getEvent().getEventId());
        if (historyEvent == null || historyEvent.getDate().after(new Date())) {
            return new Status(403, "Event does not exist or has not yet happened!");
        }
        String applicant = jwtUtil.extractUsername(jwt);
        EventMember applicantEventMember = emService.findEventForVerification(applicant, historyEvent.getEventId());
        if (applicantEventMember == null || applicantEventMember.getPhotoPath() == null) {
            return new Status(401, "Applicant was not in this event or has not yet upload photo!");
        }
        final List<EventMember> verifiers = new ArrayList<>();
        ev.getVerifications()
                .forEach((Verification verification) -> {
                    verifiers.add(emService.findEventMemberByEventIdAndUserName(historyEvent.getEventId(), verification.getVerifier()));
                });
        for (EventMember eventMember : verifiers) {
            if (eventMember.getUserName().equals(applicant)) {
                return new Status(402, "Verifiers must not include applicant him/her self!");
            }
        }
        List<Verification> checkVerifications = verificationService.findVerificationByEventIdAndUserName(ev.getEvent().getEventId(), applicant);
        for (Verification checkVerification : checkVerifications) {
            for (EventMember verifier : verifiers) {
                if (checkVerification.getVerifier().equals(verifier.getUserName())) {
                    return new Status(404, "Already apply verification to " + verifier.getUserName());
                }
            }
        }
        List<Verification> verifications = new ArrayList<>();
        verifiers.stream()
                .filter((EventMember member) -> !member.getUserName().equals(applicant))
                .collect(Collectors.toList())
                .forEach((EventMember member) -> verifications.add(verificationService.save(new Verification(historyEvent.getEventId(),
                        applicant,
                        member.getUserName(),
                        applicantEventMember.getPhotoPath()))));
        verifications
                .forEach((Verification verification) -> verificationService.save(verification));
        applicantEventMember.setIsApplied(1);
        emService.save(applicantEventMember);
        for(EventMember em : verifiers){
            SendGridEmailer.getInstance().sendEmail(subject,
                    em.getUserName(),
                    applicantEventMember.getUserName()+application);
        }
        return new Status(200, verifications);
    }

    @PostMapping("/uploadPhoto")
    public Status uploadPhoto(@RequestParam("file") MultipartFile file,
                              @RequestParam int eventId,
                              @RequestHeader("Authorization") String jwt) {
        Event event = eventService.findByEventId(eventId);
        if (event == null || event.getDate().after(new Date())) {
            return new Status(400, "Event does not exist or not happened yet!");
        }
        String applicant = jwtUtil.extractUsername(jwt);
        EventMember applicantEventMember = emService.findEventMemberByEventIdAndUserName(eventId, applicant);
        if (applicantEventMember == null) {
            return new Status(401, "Event member does not exist!");
        }
        Status status = filesUtil.uploadFile(file, verificationPath);
        if (status.getStatus() != 200) {
            return status;
        }
        applicantEventMember.setPhotoPath(status.getMsg());
        emService.save(applicantEventMember);
        return new Status(200, applicantEventMember);
    }

    @GetMapping("/myEventsToVerify")
    public Status myEventsToVerify(@RequestHeader("Authorization") String jwt) {
        String applicant = jwtUtil.extractUsername(jwt);
        List<Event> eventsToVerify = eventService.findMyEventsToVerify(applicant);
        if (eventsToVerify.isEmpty()) {
            return new Status(201, "No events to verify!");
        }
        List<EventMemberToVerify> list = new ArrayList<>();
        for (Event event : eventsToVerify) {
            List<MemberWithName> memberWithName = emService.findEventMembersWithNameByEventId(event.getEventId());
            List<Verification> verifications = verificationService.findVerificationByEventIdAndUserName(
                    event.getEventId(),
                    applicant);
            for (int k = 0; k < memberWithName.size(); k++) {
                if (memberWithName.get(k).getMember().getUserName().equals(applicant)) {
                    memberWithName.remove(k);
                    k--;
                    continue;
                }
                for (int h = 0; h < verifications.size(); h++) {
                    if (memberWithName.get(k).getMember().getUserName().equals(verifications.get(h).getVerifier())) {
                        memberWithName.remove(k);
                        k--;
                        break;
                    }
                }
            }
            if (memberWithName.isEmpty()) {
                continue;
            }
            list.add(new EventMemberToVerify(event, memberWithName));
        }
        if(list.isEmpty()){
            return new Status(202, "No events to verify!");
        }
        return new Status(200, list);
    }

    @GetMapping("/getRequests")
    public Status findRequestByVerifier(@RequestHeader("Authorization") String jwt) {
        String verifier = jwtUtil.extractUsername(jwt);
        List<Verification> verifications = verificationService.findRequestByVerifier(verifier);
        if (verifications.isEmpty()) {
            return new Status(200, "No verification requests!");
        }
        List<VerificationWithDetails> details = new ArrayList<>();
        verifications.forEach((Verification v)-> {
            v.setPhotoPath(vmAddress+v.getPhotoPath());
            String userPhoto = UserController.vmAddress+userDetailsService.getPhotoPath(v.getUserName());
            Event event = eventService.findByEventId(v.getEventId());
            String mountainName = mountainService.findMountainNameById(event.getMountainId());
            String name = userDetailsService.getName(v.getUserName());
            details.add(new VerificationWithDetails(v, name ,userPhoto, event.getDate(), mountainName));
        });
        return new Status(200, details);
    }

    @Transactional
    @PostMapping("/verify")
    public Status verify(@RequestHeader("Authorization") String jwt, @RequestBody Verification verification) throws IOException {
        if(verification.getStatus() != 1 && verification.getStatus()!=2){
            return new Status (400, "Invalid status parameter!");
        }
        String verifier = jwtUtil.extractUsername(jwt);
        Optional<Verification> verify = verificationService.findVerificationByVerId(verification.getVerId());
        if (verify.isEmpty() || !verify.get().getVerifier().equals(verifier) || verify.get().getStatus() == 1) {
            return new Status(401, "Verification does not exist or invalid verifier or has been verified!");
        }
        verify.get().setStatus(verification.getStatus());
        verificationService.save(verify.get());
        if(verification.getStatus()==2){
            return new Status (200, verify.get());
        }
        int mountainId = eventService.findMountainByEventId(verify.get().getEventId());
        achievementService.save(new Achievement(mountainId, verify.get().getUserName()));
        mountainService.addSummitReachedByOne(mountainId);
        SendGridEmailer.getInstance().sendEmail(subject,
                verify.get().getUserName(),
                userDetailsService.getName(verifier)+approved);
        return new Status(200, verify.get());
    }
}
