package com.securityApp.services;

import com.securityApp.models.Person;
import com.securityApp.models.Role;
import com.securityApp.repositories.PeopleRepository;
import com.securityApp.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class AdminServicesImpl {

    private final PeopleRepository peopleRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminServicesImpl(PeopleRepository peopleRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.peopleRepository = peopleRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public List<Person> findAll() {
        return peopleRepository.findAll();
    }

    @Transactional
    public Person findById(int id) {
        Optional<Person> person = peopleRepository.findById(id);
        return person.orElse(null);
    }

    @Transactional
    public Optional<Person> findByUsername(String username) {
        return peopleRepository.findByUsername(username);
    }

    @Transactional
    public void savePerson(Person person) {
        Optional<Person> existingPersonOpt = peopleRepository.findById(person.getId());

        if (existingPersonOpt.isPresent()) {
            Person existingPerson = existingPersonOpt.get();

            // Check if a new password is provided
            if (person.getPassword() == null || person.getPassword().isEmpty()) {
                // Retain the existing password
                person.setPassword(existingPerson.getPassword());
            } else {
                // Encode the new password
                person.setPassword(passwordEncoder.encode(person.getPassword()));
            }

            // Update other fields from the existing person
            person.setRoles(existingPerson.getRoles()); // Assuming roles should be preserved

        } else if (person.getPassword() != null && !person.getPassword().isEmpty()) {
            // Encode the new password for new user
            person.setPassword(passwordEncoder.encode(person.getPassword()));
        }

        peopleRepository.save(person);
    }

    @Transactional
    public void deleteById(int id) {
        peopleRepository.deleteById(id);
    }

    @Transactional
    public boolean usernameExists(String username) {
        return peopleRepository.findByUsername(username).isPresent();
    }

    @Transactional
    public void assignRolesToPerson(Person person, Set<Integer> roleIds) {
        Set<Role> roles = new HashSet<>();
        for (Integer roleId : roleIds) {
            roleRepository.findById(roleId).ifPresent(roles::add);
        }
        person.setRoles(roles);
        savePerson(person);
    }
}
