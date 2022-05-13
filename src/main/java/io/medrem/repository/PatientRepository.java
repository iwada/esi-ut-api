package io.medrem.repository;

import java.lang.StackWalker.Option;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.medrem.models.Patient;
import io.medrem.models.User;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Boolean existsByUser(User user);
    Optional<Patient> findByUserId(Long id);
    
}
