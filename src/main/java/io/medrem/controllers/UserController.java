package io.medrem.controllers;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.medrem.models.User;
import io.medrem.repository.UserRepository;
import io.medrem.security.services.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/v1/api/users")
public class UserController {

  @Autowired
  UserRepository userRepository;

  @GetMapping("/all")
  public String allAccess() {
    return "Public Content.";
  }


  @GetMapping(value = "/role")
  @PreAuthorize("hasRole('USER') or hasRole('RECEPTIONIST') or hasRole('PHYSICIAN') or hasRole('ADMIN')")
  @ResponseBody
  public ResponseEntity<?> currentUserRole(Authentication authentication) {
     UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
     List<String> roles = userDetails.getAuthorities().stream()
        .map(item -> item.getAuthority())
        .collect(Collectors.toList());

    return ResponseEntity.ok(Collections.singletonMap("roles",roles.get(0)));

  }

  @GetMapping("/user")
  @PreAuthorize("hasRole('USER') or hasRole('RECEPTIONIST') or hasRole('PHYSICIAN') or hasRole('ADMIN')")
  public String userAccess() {
    return "User Content.";
  }

  @GetMapping("/doc")
  @PreAuthorize("hasRole('PHYSICIAN')")
  public String phycisicianAccess() {
    return "Doctor Content.";
  }

  @GetMapping("/rec")
  @PreAuthorize("hasRole('RECEPTIONIST')")
  public String receptionistAccess() {
    return "Receptionist Content.";
  }

  @GetMapping("/admin")
  @PreAuthorize("hasRole('ADMIN')")
  public String adminAccess() {
    return "Admin Content.";
  }
}
