package io.medrem.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/v1/api/")
public class UserController {
  @GetMapping("/all")
  public String allAccess() {
    return "Public Content.";
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
