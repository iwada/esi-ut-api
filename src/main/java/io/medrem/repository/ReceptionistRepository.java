package io.medrem.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.medrem.models.Receptionist;
import io.medrem.models.User;

public interface ReceptionistRepository extends JpaRepository<Receptionist,Long> {
    Boolean existsByUser(User user);
    
}
