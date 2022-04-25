package io.medrem.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.medrem.models.Doctor;
import io.medrem.models.Schedule;
import io.medrem.payload.request.ScheduleRequest;
import io.medrem.payload.response.MessageResponse;
import io.medrem.repository.DoctorRepository;
import io.medrem.repository.ScheduleRepository;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/v1/api/doctors")
public class ScheduleController {

    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    ScheduleRepository scheduleRepository;

    @PostMapping("{doctorId}/schedule")
    @PreAuthorize("hasRole('PHYSICIAN')")
    public ResponseEntity<?> createSchedule(@Valid @RequestBody ScheduleRequest scheduleRequest,
            @PathVariable("doctorId") Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
        if (doctor == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Doctor with ID: " + doctorId + " does not exist."));
        }

        if (scheduleRequest.isStartDateValid(scheduleRequest.getSDate())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Start Date is Invalid"));
        }
        if (scheduleRequest.isEndDateValid(scheduleRequest.getSDate(), scheduleRequest.getEDate())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: End Date is Invalid"));
        }
        Schedule schedule = new Schedule(
                scheduleRequest.getStartdate(),
                scheduleRequest.getEnddate(),
                scheduleRequest.getIsAvailable(),
                scheduleRequest.getIsRecurring(),
                doctor);

        scheduleRepository.save(schedule);

        return ResponseEntity.ok(new MessageResponse("Schedule registered successfully!"));

    }

    @GetMapping("{doctorId}/schedules")
    @PreAuthorize("hasRole('PHYSICIAN')")
    public ResponseEntity<?> getDoctorsSchedules(String publisher_name, @PathVariable("doctorId") Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
        if (doctor == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Doctor with ID: " + doctorId + " does not exist."));
        }
        List<Schedule> schedules = new ArrayList<Schedule>();
        scheduleRepository.findByDoctorId(doctorId).forEach(schedules::add);
        return new ResponseEntity<>(schedules, HttpStatus.OK);
        
    }

    @DeleteMapping("{doctorsId}/schedules/{scheduleId}")
    @PreAuthorize("hasRole('PHYSICIAN')")
    public ResponseEntity<?> deleteSchedule(@PathVariable("doctorsId") long doctorId,
            @PathVariable("scheduleId") long scheduleId) {
        Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
        Schedule schedule = scheduleRepository.findById(scheduleId).orElse(null);

        if (doctor == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Doctor with ID: " + doctorId + " does not exist."));
        }
        if (doctor.getUser().getId() != doctorId) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: You can only Delete you Own Schedule"));
        }
        if (schedule == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Schedule with ID: " + scheduleId + " does not exist."));
        }
        scheduleRepository.deleteById(scheduleId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("{doctorsId}/schedules/{scheduleId}")
    @PreAuthorize("hasRole('PHYSICIAN')")
    public ResponseEntity<?> editSchedule(@PathVariable("doctorsId") long doctorId,
            @PathVariable("scheduleId") long scheduleId, @Valid @RequestBody ScheduleRequest scheduleRequest) {
        Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
        Schedule schedule = scheduleRepository.findById(scheduleId).orElse(null);

        if (doctor == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Doctor with ID: " + doctorId + " does not exist."));
        }
        if (doctor.getUser().getId() != doctorId) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: You can only Delete you Own Schedule"));
        }
        if (schedule == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Schedule with ID: " + scheduleId + " does not exist."));
        }
        schedule.setStartdate(scheduleRequest.getStartdate());
        schedule.setEnddate(scheduleRequest.getEnddate());
        schedule.setIsAvailable(scheduleRequest.getIsAvailable());
        schedule.setIsRecurring(scheduleRequest.getIsRecurring());

        scheduleRepository.save(schedule);
        return ResponseEntity.ok(new MessageResponse("Schedule updated successfully!"));
    }
}
