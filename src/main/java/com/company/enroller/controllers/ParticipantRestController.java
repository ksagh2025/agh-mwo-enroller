package com.company.enroller.controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.company.enroller.model.Participant;
import com.company.enroller.persistence.ParticipantService;

@RestController
@RequestMapping("/participants")
public class ParticipantRestController {

	@Autowired
	ParticipantService participantService;

	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<?> getParticipants() {
		Collection<Participant> participants = participantService.getAll();
		return new ResponseEntity<Collection<Participant>>(participants, HttpStatus.OK);
	}

//    @RequestMapping(value = "" , method = RequestMethod.GET)
//    public ResponseEntity<?> getParticipantSorted(
//            @RequestParam(value = "sortBy" , defaultValue = "login") String sortBy ,
//            @RequestParam(value = "sortOrder" , defaultValue = "ASC") String sortOrder ) {
//        Collection<Participant> participants = participantService.getAllSorted();
//        return new ResponseEntity<Collection<Participant>>(participants, HttpStatus.OK);
//    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getParticipant(@PathVariable("id") String login) {
        Participant participant = participantService.findByLogin(login);
        if (participant == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Participant>(participant, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> registerParticipant(@RequestBody Participant participant) {
       Participant fooundparticipant = participantService.findByLogin(participant.getLogin());
       if (fooundparticipant != null) {
           return new ResponseEntity("Already exists",HttpStatus.CONFLICT);
       }
       participantService.add(participant);
       return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}" , method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteParticipant(@PathVariable("id") String login) {
        Participant fooundparticipant = participantService.findByLogin(login);
        if (fooundparticipant == null) {
            return new ResponseEntity("Doesnt Exists",HttpStatus.NOT_FOUND);}
        participantService.delete(fooundparticipant);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{login}" , method = RequestMethod.PUT)
    public ResponseEntity<?> getParticipant(@PathVariable("login") String login , @RequestBody Participant participant) {
        Participant fooundparticipant = participantService.findByLogin(login);
        if (fooundparticipant == null) {
            return new ResponseEntity("Doesnt Exists",HttpStatus.NOT_FOUND);}
//        fooundparticipant.setPassword(participant.getPassword());
        participantService.update(participant);
        return new ResponseEntity<>(fooundparticipant,HttpStatus.OK);
    }
}
