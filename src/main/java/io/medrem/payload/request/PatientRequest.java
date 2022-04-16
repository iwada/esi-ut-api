package io.medrem.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import io.medrem.models.User;

public class PatientRequest {

    @NotBlank
    @Size(max = 50)
    private String firstname;

    @NotBlank
    @Size(max = 50)
    private String lastname;

    @NotBlank
    @Size(max = 50)
    private String mobilenumber;

    @NotBlank
    @Size(max = 50)
    private String gender;

    private User user;


    public PatientRequest(String firstname, String lastname, String mobilenumber, String gender, User user) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.mobilenumber = mobilenumber;
        this.gender = gender;
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

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    
}
