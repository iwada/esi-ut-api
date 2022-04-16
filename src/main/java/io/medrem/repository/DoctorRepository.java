package io.medrem.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.medrem.models.Doctor;
import io.medrem.models.User;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long>{
    Boolean existsByUser(User user);
    Optional<Doctor> findById(Long id);
    //List<String> findAllSchedules();

    
}
