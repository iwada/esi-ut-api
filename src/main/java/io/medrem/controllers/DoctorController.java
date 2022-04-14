package io.medrem.controllers;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.medrem.models.Doctor;
import io.medrem.models.User;
import io.medrem.payload.request.DoctorRequest;
import io.medrem.payload.response.MessageResponse;
import io.medrem.repository.DoctorRepository;
import io.medrem.repository.UserRepository;
import io.medrem.security.services.UserDetailsImpl;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/v1/api/doctors")
public class DoctorController {

    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    UserRepository userRepository;

    @PostMapping("/signup")
    public ResponseEntity<?> registerDoctor(@Valid @RequestBody DoctorRequest doctorRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        Optional<User> optUser = userRepository.findById(userDetails.getId());
        User user = optUser.get();
        if (doctorRepository.existsByUser(user)) {
            return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Doctor has already completed Registration"));
          }
    
       
        Doctor doctor = new Doctor(
            doctorRequest.getFirstname(), 
            doctorRequest.getLastname(), 
            doctorRequest.getSpecialty(),
            user);
        
            doctorRepository.save(doctor);

            return ResponseEntity.ok(new MessageResponse("Doctor registered successfully!"));
        
    }
}
