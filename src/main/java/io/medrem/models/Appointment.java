package io.medrem.models;

import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    
    @Column
    private LocalDateTime appointmentTimestamp;


    //@OneToOne(cascade = CascadeType.ALL)
    @OneToOne
    @JoinColumn(name="patient_id", referencedColumnName = "id")
    @JsonBackReference
    private Patient patient;

    //@OneToOne(cascade = CascadeType.ALL)
    @OneToOne
    @JoinColumn(name="doctor_id", referencedColumnName = "id")
    @JsonBackReference
    private Doctor doctor;

    
    @Size(max = 500)
    private String notes;

    @Size(max = 50)
    private String color;

    @Size(max = 250)
    private String label;
    


    public Appointment(LocalDateTime appointmentTimestamp, Patient patient, Doctor doctor, String notes, String color, String label) {
        this.appointmentTimestamp = appointmentTimestamp;
        this.patient = patient;
        this.doctor = doctor;
        this.notes = notes;
        this.color = color;
        this.label = label;
    }
   

    public Appointment() {
    }


    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getAppointmentTimestamp() {
        return this.appointmentTimestamp;
    }

    public void setAppointmentTimestamp(LocalDateTime appointmentTimestamp) {
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


}
