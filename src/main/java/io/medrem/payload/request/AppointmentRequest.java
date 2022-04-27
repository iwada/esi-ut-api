package io.medrem.payload.request;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import io.medrem.models.Doctor;
import io.medrem.models.Patient;

public class AppointmentRequest {

    @NotBlank
    private String appointmentTimestamp;

    private Patient patient;

    private Doctor doctor;

    @Size(max = 500)
    private String notes;

    @Size(max = 50)
    private String color;

    @Size(max = 250)
    private String label;

    private Boolean active;

    public AppointmentRequest(String appointmentTimestamp, Patient patient, Doctor doctor, String notes, String color,
            String label) {
        this.appointmentTimestamp = appointmentTimestamp;
        this.patient = patient;
        this.doctor = doctor;
        this.notes = notes;
        this.color = color;
        this.label = label;
    }

    public AppointmentRequest() {
    }
    

    public AppointmentRequest(String appointmentTimestamp, Patient patient, Doctor doctor, String notes, String color,
            String label, boolean active) {
        this.appointmentTimestamp = appointmentTimestamp;
        this.patient = patient;
        this.doctor = doctor;
        this.notes = notes;
        this.color = color;
        this.label = label;
        this.active = active;
    }


    public Boolean isActive() {
        return this.active;
    }

    public Boolean getActive() {
        return this.active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    
    

    public String getAppointmentTimestamp() {
        return this.appointmentTimestamp;
    }

    public void setAppointmentTimestamp(String appointmentTimestamp) {
        this.appointmentTimestamp = appointmentTimestamp;
    }

    public Patient getPatient() {
        return this.patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return this.doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public String getNotes() {
        return this.notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    static Date parsedDate;

    public static Date getDateFromString(String string) {
        SimpleDateFormat formatter = new SimpleDateFormat("E, MMM dd yyyy HH:mm:ss");

        try {
            parsedDate = formatter.parse(string);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return parsedDate;

    }

}
