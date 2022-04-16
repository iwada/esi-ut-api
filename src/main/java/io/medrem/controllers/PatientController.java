package io.medrem.controllers;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.medrem.models.Patient;
import io.medrem.models.User;
import io.medrem.payload.request.PatientRequest;
import io.medrem.payload.response.MessageResponse;
import io.medrem.repository.PatientRepository;
import io.medrem.repository.UserRepository;
import io.medrem.security.services.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/v1/api/patients")
public class PatientController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PatientRepository patientRepository;

    @PostMapping("/signup")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> registerPatient(@Valid @RequestBody PatientRequest patientRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        Optional<User> optUser = userRepository.findById(userDetails.getId());
        User user = optUser.get();
        if (patientRepository.existsByUser(user)) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Patient has already completed Registration"));
        }

        Patient patient = new Patient(
                patientRequest.getFirstname(),
                patientRequest.getLastname(),
                patientRequest.getMobilenumber(),
                patientRequest.getGender(),
                user);
                

        patientRepository.save(patient);

        return ResponseEntity.ok(new MessageResponse("Patient registered successfully!"));

    }

    @PutMapping("{patientId}/edit")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> editDoctor(@PathVariable("patientId") long patientId, @Valid @RequestBody PatientRequest patientRequest) {
        Patient patient = patientRepository.findById(patientId).orElse(null);
        if (patient == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Patient with ID: " + patientId + " does not exist."));
        }
        if (patient.getUser().getId() != patientId) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: You can only Edit Your Own Details."));
        }
        
        patient.setFirstname(patientRequest.getFirstname());
        patient.setLastname(patientRequest.getLastname());
        patient.setMobilenumber(patientRequest.getMobilenumber());
        patient.setGender(patientRequest.getGender());
        patientRepository.save(patient);
        return ResponseEntity.ok(new MessageResponse("Patient Details updated successfully!"));
    }
    
}
