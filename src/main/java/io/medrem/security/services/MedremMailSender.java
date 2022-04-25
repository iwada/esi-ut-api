package io.medrem.security.services;

import org.jobrunr.jobs.mappers.JobMapper;
import org.jobrunr.storage.InMemoryStorageProvider;
import org.jobrunr.storage.StorageProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MedremMailSender {

    @Autowired
    private JavaMailSender mailSender;

    

    // Making the decision of using this class to "setup" jobRunner also 
    @Bean
    public StorageProvider storageProvider(JobMapper jobMapper) {
        InMemoryStorageProvider storageProvider = new InMemoryStorageProvider();
        storageProvider.setJobMapper(jobMapper);
        return storageProvider;
    }

    
    public void sendAppointmentNotification(String to, String doctor, String appointmentDate,String name) {
        String from = "<ESI-TeamN>esi.team.n@gmail.com";
     
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Appointment Successfully Booked");
        message.setText(String.format("Hello %s, \nAn Appointment  with Dr. %s for %s has been booked for you. See you then!. \n\nKind Regards\nESI-Team N",name,doctor,appointmentDate));

        mailSender.send(message);
    }

    public void sendAppointmentModifiedNotification(String to, String doctor, String appointmentDate,String name) {
        String from = "<ESI-TeamN>esi.team.n@gmail.com";
     
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Appointment Has been Modified");
        message.setText(String.format("Hello %s, \nYour Appointment  with Dr. %s has been modified. New Schedule is %s. See you then!. \n\nKind Regards\nESI-Team N",name,doctor,appointmentDate));

        mailSender.send(message);
    }

    public void sendAppointmentDeletedNotification(String to, String doctor, String appointmentDate,String name) {
        String from = "<ESI-TeamN>esi.team.n@gmail.com";
     
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Appointment Has been Deleted");
        message.setText(String.format("Hello %s, \nYour Appointment  with Dr. %s for %s has been Deleted.!. \n\nKind Regards\nESI-Team N",name,doctor,appointmentDate));

        mailSender.send(message);
    }
    
}