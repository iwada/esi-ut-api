package io.medrem.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import io.medrem.models.Appointment;
import io.medrem.models.Patient;
import io.medrem.models.User;
import io.medrem.payload.request.PatientRequest;
import io.medrem.payload.response.MessageResponse;
import io.medrem.payload.response.PatientResponse;
import io.medrem.repository.AppointmentRepository;
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

    @Autowired
    AppointmentRepository appointmentRepository;

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
    @PreAuthorize("hasRole('USER') or hasRole('RECEPTIONIST')")
    public ResponseEntity<?> editPatient(@PathVariable("patientId") long patientId, @Valid @RequestBody PatientRequest patientRequest) {
        Patient patient = patientRepository.findById(patientId).orElse(null);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        Optional<User> optUser = userRepository.findById(userDetails.getId());
        User user = optUser.get();
        if (patient == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Patient with ID: " + patientId + " does not exist."));
        }
        if (patient.getUser().getId() != user.getId())  {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: You can only edit Your Own Details."));
        }
        
        patient.setFirstname(patientRequest.getFirstname());
        patient.setLastname(patientRequest.getLastname());
        patient.setMobilenumber(patientRequest.getMobilenumber());
        patient.setGender(patientRequest.getGender());
        patientRepository.save(patient);
        return ResponseEntity.ok(new MessageResponse("Patient Details updated successfully!"));
    }


    @GetMapping("{patientUserId}")
    @PreAuthorize("hasRole('USER') or hasRole('RECEPTIONIST') ")
    public ResponseEntity<?> showPatient(@PathVariable("patientUserId") long patientUserId) {
        Patient patient = patientRepository.findByUserId(patientUserId).orElse(null);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        Optional<User> optUser = userRepository.findById(userDetails.getId());
        User user = optUser.get();

    
        if (patient == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Patient with user ID: " + patientUserId + " does not exist."));
        }    

         // So Only Receptionist, Doctors should be able to 
         if (String.valueOf(user.getRoles().iterator().next().getName()) == "ROLE_USER") {
            if (patient.getUser().getId() != user.getId()) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: You can only Delete Your Own Details."));
            }
        }
        List<Appointment> appointments = new ArrayList<>();
        appointmentRepository.findByPatientId(patient.getId()).forEach(appointments::add);
        return ResponseEntity.ok(new PatientResponse(
            patient.getId(),
            patient.getFirstname(), 
            patient.getLastname(), 
            patient.getMobilenumber(),
            patient.getGender(),
            appointments));
    }

    @GetMapping("")
    @PreAuthorize("hasRole('RECEPTIONIST') or hasRole('PHYSICIAN')")
    public ResponseEntity<?> getAllPatients(){
        List<Patient> patients = patientRepository.findAll();
        return new ResponseEntity<>(patients, HttpStatus.OK);
    }

    @GetMapping("/getId")
    @PreAuthorize("hasRole('USER') or hasRole('PHYSICIAN')")
    public ResponseEntity<?> getPatientID(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        Optional<User> optUser = userRepository.findById(userDetails.getId());
        User user = optUser.get();
        Patient patient = patientRepository.findByUserId(user.getId()).orElse(null);
        return new ResponseEntity<>(patient, HttpStatus.OK);


    }

    
    
}
