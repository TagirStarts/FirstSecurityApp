package com.securityApp.services;

import com.securityApp.models.Person;
import com.securityApp.models.Role;
import com.securityApp.repositories.PeopleRepository;
import com.securityApp.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class RegistrationService {

    private final PeopleRepository peopleRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Autowired
    public RegistrationService(PeopleRepository peopleRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.peopleRepository = peopleRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public void register(Person person) {
        if (peopleRepository.findByUsername(person.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        Optional<Role> userRole = roleRepository.findByName("ROLE_USER");
        userRole.ifPresent(role -> person.getRoles().add(role));
        peopleRepository.save(person);
    }
}
