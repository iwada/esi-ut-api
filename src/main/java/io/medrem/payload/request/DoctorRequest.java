package io.medrem.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import io.medrem.models.User;

public class DoctorRequest {
    @Size(max = 50)
    private String firstname;

    @NotBlank
    @Size(max = 50)
    private String lastname;

    @NotBlank
    @Size(max = 50)
    private String specialty;

    private User user;

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public String getSpecialty() {
        return this.specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }


    public DoctorRequest(String firstname, String lastname, String specialty) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.specialty = specialty;
    }
   
    
}
