package io.medrem.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.medrem.models.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
}
