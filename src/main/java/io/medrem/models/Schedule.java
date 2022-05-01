package io.medrem.models;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "schedules")
public class Schedule{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;


    @NotBlank
    @Size(max = 50)
    private String startdate;

    @NotBlank
    @Size(max = 50)
    private String enddate;


    @Column(columnDefinition = "varchar(255) default '0'")
    private String isAvailable;

    @Column(columnDefinition = "varchar(255) default '0'")
    private String isRecurring;


    //@OneToMany(cascade = CascadeType.ALL)
    //@JoinColumn(name="doctor_id", referencedColumnName = "id")
    //private Doctor doctor;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="doctor_id", referencedColumnName = "id")
    private Doctor  doctor;

 
   

    public Schedule(String startdate, String enddate, String isAvailable, String isRecurring, Doctor doctor) {
        this.startdate = startdate;
        this.enddate = enddate;
        this.isAvailable = isAvailable;
        this.isRecurring = isRecurring;
        this.doctor = doctor;
    }


    public Schedule() {
    }
    

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStartdate() {
        return this.startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getEnddate() {
        return this.enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public String getIsAvailable() {
        return this.isAvailable;
    }

    public void setIsAvailable(String isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getIsRecurring() {
        return this.isRecurring;
    }

    public void setIsRecurring(String isRecurring) {
        this.isRecurring = isRecurring;
    }

    public Doctor getDoctor() {
        return this.doctor;
    }
   

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }
    

    
   

    
}
