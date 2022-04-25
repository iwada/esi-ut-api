package io.medrem.controllers;



import java.time.LocalDateTime;

import java.util.Optional;

import javax.validation.Valid;

import org.jobrunr.scheduling.JobScheduler;
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
import io.medrem.security.services.MedremMailSender;
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

    @Autowired
    MedremMailSender medremMailSender;

    @Autowired
    private JobScheduler jobScheduler;

    String regex = "(\\d{4})-(\\d{2})-(\\d{2})[T](\\d{2}):(\\d{2}):(\\d{2})";

    // 25.04.22 - Maybe i implement a generic create appointment at /appointment/new
    // that takes patient_id,doctor_id? discuss merits
    // with team morrow. But i kind of prefer this curent pattern.
    @PostMapping("/patients/{patientId}/appointment")
    @PreAuthorize("hasRole('USER') or hasRole('RECEPTIONIST') or hasRole('ADMIN') or hasRole('PHYSICIAN')")
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

        // Send Appointment Confirmation Email in the Background
        jobScheduler.enqueue(() -> medremMailSender.sendAppointmentNotification(
                String.valueOf(patient.getUser().getEmail()),
                String.valueOf(doctor.getFirstname() + " " + doctor.getLastname()),
                String.valueOf(appointmentSchedule), String.valueOf(patient.getFirstname())));

        return ResponseEntity.ok(new MessageResponse("Appointment registered successfully!"));

    }

    @PutMapping("/patients/{patientId}/appointments/{appointmentId}/edit")
    @PreAuthorize("hasRole('USER') or hasRole('RECEPTIONIST') or hasRole('ADMIN') or hasRole('PHYSICIAN')")
    public ResponseEntity<?> editAppointment(@PathVariable("patientId") long patientId,
            @PathVariable("appointmentId") long appointmentId,
            @Valid @RequestBody AppointmentRequest appointmentRequest) {
        Patient patient = patientRepository.findById(patientId).orElse(null);
        Appointment appointment = appointmentRepository.findById(appointmentId).orElse(null);
        Doctor doctor = doctorRepository.findById(appointmentRequest.getDoctor().getId()).orElse(null);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        Optional<User> optUser = userRepository.findById(userDetails.getId());
        User user = optUser.get();
     
        if (patient == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Patient with ID: " + patientId + " does not exist."));
        }
        
        if (String.valueOf(user.getRoles().iterator().next().getName()) == "ROLE_USER") {
            if (patient.getUser().getId() != user.getId()) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: You can only edit Your Own Details."));
            }
        }
        if (appointment == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Appointment with ID: " + appointmentId + " does not exist."));
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

        // Send Appointment Confirmation Email in the Background
        jobScheduler.enqueue(() -> medremMailSender.sendAppointmentModifiedNotification(
                String.valueOf(patient.getUser().getEmail()),
                String.valueOf(doctor.getFirstname() + " " + doctor.getLastname()),
                String.valueOf(appointmentSchedule), String.valueOf(patient.getFirstname())));
        return ResponseEntity.ok(new MessageResponse("Appointment updated successfully!"));
    }

    @DeleteMapping("/patients/{patientId}/appointments/{appointmentId}")
    @PreAuthorize("hasRole('USER') or hasRole('RECEPTIONIST') or hasRole('ADMIN')")
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
        // So Only Receptionist, Doctors should be able to delete every appointment
        if (String.valueOf(user.getRoles().iterator().next().getName()) == "ROLE_USER") {
            if (patient.getUser().getId() != user.getId()) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: You can only Delete Your Own Details."));
            }
        }
        if (appointment == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Appointment with ID: " + appointmentId + " does not exist."));
        }
        
        Doctor doctor = doctorRepository.findById(appointment.getDoctor().getId()).orElse(null);
        jobScheduler.enqueue(() -> medremMailSender.sendAppointmentDeletedNotification(
                String.valueOf(patient.getUser().getEmail()),
                String.valueOf(doctor.getFirstname() + " " + doctor.getLastname()),
                String.valueOf(appointment.getAppointmentTimestamp()), String.valueOf(patient.getFirstname())));

        appointmentRepository.deleteById(appointmentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
