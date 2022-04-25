package io.medrem.payload.response;

import java.util.List;

import io.medrem.models.Appointment;

public class PatientResponse {
    private Long Id;
    private String firstname;
    private String lastname;
    private String mobilenumber;
    private String gender;
    private List<Appointment> appointments;


    public PatientResponse(Long Id, String firstname, String lastname, String mobilenumber, String gender, List<Appointment> appointments) {
        this.Id = Id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.mobilenumber = mobilenumber;
        this.gender = gender;
        this.appointments = appointments;
    }


    public PatientResponse() {
    }


    public Long getId() {
        return this.Id;
    }

    public void setId(Long Id) {
        this.Id = Id;
    }

    public String getFirstname() {
        return this.firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return this.lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getMobilenumber() {
        return this.mobilenumber;
    }

    public void setMobilenumber(String mobilenumber) {
        this.mobilenumber = mobilenumber;
    }

    public String getGender() {
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public List<Appointment> getAppointments() {
        return this.appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    
}
