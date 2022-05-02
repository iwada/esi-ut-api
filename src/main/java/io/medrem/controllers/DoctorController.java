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
import io.medrem.models.Doctor;
import io.medrem.models.Schedule;
import io.medrem.models.User;
import io.medrem.payload.request.DoctorRequest;
import io.medrem.payload.response.DoctorResponse;
import io.medrem.payload.response.MessageResponse;
import io.medrem.repository.AppointmentRepository;
import io.medrem.repository.DoctorRepository;
import io.medrem.repository.ScheduleRepository;
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

    @Autowired
    ScheduleRepository scheduleRepository;

    @Autowired
    AppointmentRepository appointmentRepository;

    @PostMapping("/signup")
    @PreAuthorize("hasRole('PHYSICIAN')")
    public ResponseEntity<?> registerDoctor(@Valid @RequestBody DoctorRequest doctorRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        Optional<User> optUser = userRepository.findById(userDetails.getId());
        User user = optUser.get();
        if (Boolean.TRUE.equals(doctorRepository.existsByUser(user))) {
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

    @PutMapping("{doctorsId}/edit")
    @PreAuthorize("hasRole('PHYSICIAN')")
    public ResponseEntity<?> editDoctor(@PathVariable("doctorsId") long doctorId, @Valid @RequestBody DoctorRequest doctorRequest) {
        Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        Optional<User> optUser = userRepository.findById(userDetails.getId());
        User user = optUser.get();

        if (doctor == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Doctor with ID: " + doctorId + " does not exist."));
        }
        if (doctor.getUser().getId() != user.getId()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: You can only Edit Your Own Details."));
        }
        
        doctor.setFirstname(doctorRequest.getFirstname());
        doctor.setLastname(doctorRequest.getLastname());
        doctor.setSpecialty(doctorRequest.getSpecialty());
        doctorRepository.save(doctor);
        return ResponseEntity.ok(new MessageResponse("Doctor Details updated successfully!"));
    }


    @GetMapping("{doctorsId}")
    @PreAuthorize("hasRole('PHYSICIAN')")
    public ResponseEntity<?> showDoctor(@PathVariable("doctorsId") long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        Optional<User> optUser = userRepository.findById(userDetails.getId());
        User user = optUser.get();

        if (doctor == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Doctor with ID: " + doctorId + " does not exist."));
        }
        if (doctor.getUser().getId() != user.getId()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: You can only Edit Your Own Details."));
        }
        
        List<Schedule> schedules = new ArrayList<Schedule>();
        scheduleRepository.findByDoctorId(doctorId).forEach(schedules::add);

        List<Appointment> appointments = new ArrayList<Appointment>();
        appointmentRepository.findByDoctorId(doctorId).forEach(appointments::add);
        return ResponseEntity.ok(new DoctorResponse(
                         doctor.getId(),
                         doctor.getSpecialty(), 
                         doctor.getLastname(), 
                         doctor.getFirstname(),
                         schedules,
                         appointments));
    }


    @GetMapping("")
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public ResponseEntity<?> getAllDoctors(){
        List<Doctor> doctors = doctorRepository.findAll();
        return new ResponseEntity<>(doctors, HttpStatus.OK);
    }

    
}
