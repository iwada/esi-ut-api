package io.medrem.payload.response;

import java.util.List;

import io.medrem.models.Appointment;
import io.medrem.models.Schedule;

public class DoctorResponse {
    private Long Id;
    private String firstName;
    private String lastName;
    private String specialty;
    private List<Schedule> schedules;
    private List<Appointment> appointments;


    public DoctorResponse(Long Id, String firstName, String lastName, String specialty, List<Schedule> schedules, List<Appointment> appointments) {
        this.Id = Id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialty = specialty;
        this.schedules = schedules;
        this.appointments = appointments;
    }


    public Long getId() {
        return this.Id;
    }

    public void setId(Long Id) {
        this.Id = Id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSpecialty() {
        return this.specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public List<Schedule> getSchedules() {
        return this.schedules;
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
    }


    public List<Appointment> getAppointments() {
        return this.appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }
    

    
    
}
