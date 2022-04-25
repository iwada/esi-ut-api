package io.medrem.controllers;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.medrem.models.Receptionist;
import io.medrem.models.User;
import io.medrem.payload.request.ReceptionistRequest;
import io.medrem.payload.response.MessageResponse;
import io.medrem.payload.response.ReceptionistResponse;
import io.medrem.repository.ReceptionistRepository;
import io.medrem.repository.UserRepository;
import io.medrem.security.services.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/v1/api/receptionists")
public class ReceptionistController {


    @Autowired
    UserRepository userRepository;

    @Autowired
    ReceptionistRepository receptionistRepository;
    
    @PostMapping("/signup")
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public ResponseEntity<?> registerReceptionist(@Valid @RequestBody ReceptionistRequest receptionistRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        Optional<User> optUser = userRepository.findById(userDetails.getId());
        User user = optUser.get();
        if (receptionistRepository.existsByUser(user)) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Receptionist has already completed Registration"));
        }

        Receptionist patient = new Receptionist(
                receptionistRequest.getFirstname(),
                receptionistRequest.getLastname(),
                receptionistRequest.getMobilenumber(),
                receptionistRequest.getGender(),
                user);
                

        receptionistRepository.save(patient);

        return ResponseEntity.ok(new MessageResponse("Receptionist registered successfully!"));

    }

    @PutMapping("{receptionistId}/edit")
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public ResponseEntity<?> editReceptionist(@PathVariable("receptionistId") long receptionistId, @Valid @RequestBody ReceptionistRequest receptionistRequest) {
        Receptionist receptionist = receptionistRepository.findById(receptionistId).orElse(null);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        Optional<User> optUser = userRepository.findById(userDetails.getId());
        User user = optUser.get();
        if (receptionist == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Receptionist with ID: " + receptionistId + " does not exist."));
        }
        if (receptionist.getUser().getId() != user.getId()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: You can only edit Your Own Details."));
        }
        
        receptionist.setFirstname(receptionistRequest.getFirstname());
        receptionist.setLastname(receptionistRequest.getLastname());
        receptionist.setMobilenumber(receptionistRequest.getMobilenumber());
        receptionist.setGender(receptionistRequest.getGender());
        receptionistRepository.save(receptionist);
        return ResponseEntity.ok(new MessageResponse("Receptionist Details updated successfully!"));
    }


    @GetMapping("{receptionistId}")
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public ResponseEntity<?> showReceptionist(@PathVariable("receptionistId") long receptionistId, @RequestBody ReceptionistRequest receptionistRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        Optional<User> optUser = userRepository.findById(userDetails.getId());
        User user = optUser.get();

        Receptionist receptionist = receptionistRepository.findById(receptionistId).orElse(null);

    
        if (receptionist == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Receptionist with ID: " + receptionistId + " does not exist."));
        }    
        

        if (receptionist.getUser().getId() != user.getId()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: You can only View Your Own Details."));
        }
       
        return ResponseEntity.ok(new ReceptionistResponse(
            receptionist.getId(),
            receptionist.getFirstname(), 
            receptionist.getLastname(), 
            receptionist.getMobilenumber(),
            receptionist.getGender()
            ));
    }

    
}
