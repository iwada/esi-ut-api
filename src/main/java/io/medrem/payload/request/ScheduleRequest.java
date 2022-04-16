package io.medrem.payload.request;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import io.medrem.models.Doctor;

public class ScheduleRequest {

    @NotBlank
    @Size(max = 15)
    @Pattern(regexp = "^\\d{2}/\\d{2}/\\d{4}$")
    private String startdate;

    @NotBlank
    @Size(max = 15)
    @Pattern(regexp = "^\\d{2}/\\d{2}/\\d{4}$")
    private String enddate;

    @Size(max = 1)
    private String isAvailable;

    @Size(max = 1)
    private String isRecurring;

    private Doctor doctor;

    public ScheduleRequest(String startdate, String enddate, String isAvailable, String isRecurring, Doctor doctor) {
        this.startdate = startdate;
        this.enddate = enddate;
        this.isAvailable = isAvailable;
        this.isRecurring = isRecurring;
        this.doctor = doctor;
    }

    public String getStartdate() {
        String dayWeekText = "";
        try {
            Date parsedEndDate = new SimpleDateFormat("dd/MM/yyyy").parse(startdate);
            dayWeekText = new SimpleDateFormat("EEEE").format(parsedEndDate);
        } catch (Exception e) {
}
        return this.startdate = dayWeekText;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getEnddate() {
        String dayWeekText = "";
        try {
            Date parsedEndDate = new SimpleDateFormat("dd/MM/yyyy").parse(enddate);
            dayWeekText = new SimpleDateFormat("EEEE").format(parsedEndDate);
        } catch (Exception e) {
        }
        return this.enddate = dayWeekText;
    }

    public String getEDate() {
        return this.enddate;
    }

    public String getSDate() {
        return this.startdate;
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

    public Boolean isStartDateValid(String date) {
        Date parsedDate = new Date();
        try {
            parsedDate = new SimpleDateFormat("dd/MM/yyyy").parse(date);
            System.out.println(parsedDate);
        } catch (Exception e) {
        }
        Date currentDate = new Date();
        System.out.println(date);
        System.out.println(".........");
        System.out.println(currentDate);
        if (currentDate.after(parsedDate)) {
            return true;
        }
        return false;
    }

    public Boolean isEndDateValid(String startDate, String endDate) {
        Date parsedEDate = new Date();
        Date parsedSDate = new Date();
        try {
            parsedEDate = new SimpleDateFormat("dd/MM/yyyy").parse(endDate);
            parsedSDate = new SimpleDateFormat("dd/MM/yyyy").parse(startDate);
        } catch (Exception e) {
        }
        Date currentDate = new Date();
        if (parsedSDate.after(currentDate) && parsedEDate.compareTo(parsedSDate) < 0) {
            return true;
        }
        return false;
    }
}
