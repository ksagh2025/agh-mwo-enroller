package com.company.enroller.controllers;
import java.util.Collection;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/meetings")
public class MeetingRestController {

    @Autowired
    MeetingService meetingService;
    @Autowired
    private ParticipantService participantService;


//    @RequestMapping(value = "", method = RequestMethod.GET)
//    public ResponseEntity<?> getMeetings() {
//        Collection<Meeting> meetings = meetingService.getAll();
//        return new ResponseEntity<Collection<Meeting>>(meetings, HttpStatus.OK);
//    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetingsSortedFiltered(
            @RequestParam(value = "searchTitle" , defaultValue = "") String searchTitle,
            @RequestParam(value = "searchDescription" , defaultValue = "") String searchDescription,
            @RequestParam(value = "sortBy" , defaultValue = "id") String sortBy,
            @RequestParam(value = "sortOrder" , defaultValue = "ASC") String sortOrder
    ) {
        Collection<Meeting> meetings = meetingService.getAll(searchTitle, searchDescription, sortBy, sortOrder);
        return new ResponseEntity<Collection<Meeting>>(meetings, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getMeeting(@PathVariable("id") Long id) {
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> registerMeeting(@RequestBody Meeting meeting) {
        Meeting foundMeeting = meetingService.findById(meeting.getId());
        if (foundMeeting != null) {
            return new ResponseEntity("Already exists",HttpStatus.CONFLICT);
        }
        meetingService.add(meeting);
        return new ResponseEntity<>(meeting , HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}" , method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteMeeting(@PathVariable("id") Long id) {
        Meeting foundMeeting = meetingService.findById(id);
        if (foundMeeting == null) {
            return new ResponseEntity("Doesnt Exists",HttpStatus.NOT_FOUND);}
        meetingService.delete(foundMeeting);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}" , method = RequestMethod.PUT)
    public ResponseEntity<?> getMeeting(@PathVariable("id") Long id , @RequestBody Meeting meeting) {
        Meeting foundMeeting = meetingService.findById(id);
        if (foundMeeting == null) {
            return new ResponseEntity("Doesnt Exists",HttpStatus.NOT_FOUND);}
        meetingService.update(meeting);
        return new ResponseEntity<>(foundMeeting,HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/participants" , method = RequestMethod.POST)
    public ResponseEntity<?> addParticipant(
            @PathVariable("id") Long id,
            @RequestBody Participant participant) {

        Meeting foundMeeting = meetingService.findById(id);
        if (foundMeeting == null) {
            return new ResponseEntity("Meeting Doesnt Exists",HttpStatus.NOT_FOUND);}

        Participant foundParticipant = participantService.findByLogin(participant.getLogin());
        if (foundParticipant == null) {
            return new ResponseEntity("Participant Doesnt Exists",HttpStatus.NOT_FOUND);
        }

        foundMeeting.addParticipant(participant);
        meetingService.update(foundMeeting);
        return new ResponseEntity<>(foundMeeting,HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/participants/{login}" , method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteParticipant(
            @PathVariable("id") Long id,
            @PathVariable("login")  String login) {

        Meeting foundMeeting = meetingService.findById(id);
        if (foundMeeting == null) {
            return new ResponseEntity("Meeting Doesnt Exists",HttpStatus.NOT_FOUND);}

        Participant foundParticipant = participantService.findByLogin(login);
        if (foundParticipant == null) {
            return new ResponseEntity("Participant Doesnt Exists",HttpStatus.NOT_FOUND);
        }

        boolean isMeetingParticipant = foundMeeting.getParticipants().contains(foundParticipant);
        if (!isMeetingParticipant) {
            return new ResponseEntity("Participant Not On Meetings List",HttpStatus.NOT_FOUND);
        }

        foundMeeting.removeParticipant(foundParticipant);
        meetingService.update(foundMeeting);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/participants", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetingParticipants(
            @PathVariable("id") Long id) {

        Meeting foundMeeting = meetingService.findById(id);
        if (foundMeeting == null) {
            return new ResponseEntity("Meeting Doesnt Exists",HttpStatus.NOT_FOUND);
        }
        Collection<Participant> participants = foundMeeting.getParticipants();
        return new ResponseEntity<Collection<Participant>>(participants, HttpStatus.OK);
    }
}
