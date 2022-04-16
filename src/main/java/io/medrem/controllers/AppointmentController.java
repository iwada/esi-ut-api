package io.medrem.controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.medrem.models.Appointment;
import io.medrem.models.Doctor;
import io.medrem.models.Patient;
import io.medrem.models.User;
import io.medrem.payload.request.AppointmentRequest;
import io.medrem.payload.response.MessageResponse;
import io.medrem.repository.AppointmentRepository;
import io.medrem.repository.DoctorRepository;
import io.medrem.repository.PatientRepository;
import io.medrem.repository.UserRepository;
import io.medrem.security.services.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/v1/api")
public class AppointmentController {

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    AppointmentRepository appointmentRepository;

    @Autowired
    UserRepository userRepository;

    String regex = "(\\d{4})-(\\d{2})-(\\d{2})[T](\\d{2}):(\\d{2}):(\\d{2})";

    

    @PostMapping("/patients/{patientId}/appointment")
    @PreAuthorize("hasRole('USER') or hasRole('RECEPTIONIST') or hasRole('ADMIN')")
    public ResponseEntity<?> createAppointment(@Valid @RequestBody AppointmentRequest appointmentRequest,
            @PathVariable("patientId") long patientId) {
        Patient patient = patientRepository.findById(patientId).orElse(null);
        Doctor doctor = doctorRepository.findById(appointmentRequest.getDoctor().getId()).orElse(null);

        if (doctor == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Doctor with the Provided ID does not exist."));
        }

        if (patient == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Patient with ID: " + patientId + " does not exist."));
        }

        LocalDateTime appointmentSchedule;
        if (appointmentRequest.getAppointmentTimestamp().matches(regex)) {
            appointmentSchedule = LocalDateTime.parse(appointmentRequest.getAppointmentTimestamp());
        } else {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Invalid Date Format"));
        }

        if (patient.getUser().getId() != patientId) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: You can only create Appointment for yourSelf."));
        }

        Appointment appointment = new Appointment(
                appointmentSchedule,
                patient,
                doctor,
                appointmentRequest.getNotes(),
                appointmentRequest.getColor(),
                appointmentRequest.getLabel());

        appointmentRepository.save(appointment);

        return ResponseEntity.ok(new MessageResponse("Appointment registered successfully!"));

    }

    @PutMapping("/patients/{patientId}/appointments/{appointmentId}/edit")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> editAppointment(@PathVariable("patientId") long patientId,
            @PathVariable("appointmentId") long appointmentId,
            @Valid @RequestBody AppointmentRequest appointmentRequest) {
        Patient patient = patientRepository.findById(patientId).orElse(null);
        Appointment appointment = appointmentRepository.findById(appointmentId).orElse(null);
        Doctor doctor = doctorRepository.findById(appointmentRequest.getDoctor().getId()).orElse(null);


        if (patient == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Patient with ID: " + patientId + " does not exist."));
        }

        if (patient.getUser().getId() != patientId) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: You can only edit Your Own Details."));
        }
        if (appointment == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Schedule with ID: " + appointmentId + " does not exist."));
        }

        LocalDateTime appointmentSchedule;
        if (appointmentRequest.getAppointmentTimestamp().matches(regex)) {
            appointmentSchedule = LocalDateTime.parse(appointmentRequest.getAppointmentTimestamp());
        } else {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Invalid Date Format"));
        }
        appointment.setAppointmentTimestamp(appointmentSchedule);
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setNotes(appointmentRequest.getNotes());
        appointment.setColor(appointmentRequest.getColor());
        appointment.setLabel(appointmentRequest.getLabel());

        appointmentRepository.save(appointment);
        return ResponseEntity.ok(new MessageResponse("Appointment updated successfully!"));
    }

    @DeleteMapping("/patients/{patientId}/appointments/{appointmentId}")
    @PreAuthorize("hasRole('USER') or hasRole('RECEPTIONIST') or hasRole('PHYSICIAN') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteAppointment(@PathVariable("patientId") long patientId,
            @PathVariable("appointmentId") long appointmentId) {
        Patient patient = patientRepository.findById(patientId).orElse(null);
        Appointment appointment = appointmentRepository.findById(appointmentId).orElse(null);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
    Optional<User> optUser = userRepository.findById(userDetails.getId());
    User user = optUser.get();

        if (patient == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Patient with ID: " + patient + " does not exist."));
        }
        if (user.getId() != patientId) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: You can only Delete Your Own Details."));
        }
        if (appointment == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Appointment with ID: " + appointmentId + " does not exist."));
        }
        appointmentRepository.deleteById(appointmentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
