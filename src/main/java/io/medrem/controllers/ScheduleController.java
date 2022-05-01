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
import io.medrem.models.User;
import io.medrem.payload.request.ScheduleRequest;
import io.medrem.payload.response.MessageResponse;
import io.medrem.repository.DoctorRepository;
import io.medrem.repository.ScheduleRepository;
import io.medrem.repository.UserRepository;
import io.medrem.security.services.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/v1/api/doctors")
public class ScheduleController {

    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    ScheduleRepository scheduleRepository;

    @Autowired
    UserRepository userRepository;

    String ERROR_PRE = "Error: Doctor with ID: ";
    String ERROR_POST = " does not exist.";

    @PostMapping("{doctorId}/schedule")
    @PreAuthorize("hasRole('PHYSICIAN')")
    public ResponseEntity<?> createSchedule(@Valid @RequestBody ScheduleRequest scheduleRequest,
            @PathVariable("doctorId") Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
        if (doctor == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(ERROR_PRE + doctorId + ERROR_POST));
        }

        if (Boolean.TRUE.equals(scheduleRequest.isStartDateValid(scheduleRequest.getSDate()))) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Start Date is Invalid"));
        }
        if (Boolean.TRUE.equals(scheduleRequest.isEndDateValid(scheduleRequest.getSDate(), scheduleRequest.getEDate()))) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: End Date is Invalid"));
        }
        Schedule schedule = new Schedule(
                (scheduleRequest.getIsRecurring().equals("1") ? scheduleRequest.getStartdate() : scheduleRequest.getSDate()),
                (scheduleRequest.getIsRecurring().equals("1") ? scheduleRequest.getEnddate() : scheduleRequest.getEDate()),
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
                    .body(new MessageResponse(ERROR_PRE + doctorId + ERROR_POST));
        }

        if (doctor.getUser().getId() != doctorId) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: You can only Fetch your Own Schedule"));
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
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        Optional<User> optUser = userRepository.findById(userDetails.getId());
        User user = optUser.get();

        if (doctor == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(ERROR_PRE + doctorId + ERROR_POST));
        }

   
        if (doctor.getUser().getId() != user.getId()){
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
                    .body(new MessageResponse(ERROR_PRE + doctorId + " does not exist."));
        }
        if (doctor.getUser().getId() != doctorId) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: You can only Edit you Own Schedule"));
        }
        if (schedule == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Schedule with ID: " + scheduleId + " does not exist."));
        }
        schedule.setStartdate((scheduleRequest.getIsRecurring().equals("1") ? scheduleRequest.getStartdate() : scheduleRequest.getSDate()));
        schedule.setEnddate((scheduleRequest.getIsRecurring().equals("1") ? scheduleRequest.getEnddate() : scheduleRequest.getEDate()));
        schedule.setIsAvailable(scheduleRequest.getIsAvailable());
        schedule.setIsRecurring(scheduleRequest.getIsRecurring());

        scheduleRepository.save(schedule);
        return ResponseEntity.ok(new MessageResponse("Schedule updated successfully!"));
    }
}
