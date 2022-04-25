package io.medrem.payload.response;

public class ReceptionistResponse {
    private Long Id;
    private String firstname;
    private String lastname;
    private String mobilenumber;
    private String gender;


    public ReceptionistResponse(Long id, String firstname, String lastname, String mobilenumber, String gender) {
        this.Id = Id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.mobilenumber = mobilenumber;
        this.gender = gender;
    }


    public ReceptionistResponse() {
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
    
}
