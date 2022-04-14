package io.medrem.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.medrem.models.Doctor;
import io.medrem.models.User;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long>{
    //Optional<Doctor> findByUser(User user);
    //User findByUser(User user);
    Boolean existsByUser(User user);
    
}
