package com.securityApp.services;

import com.securityApp.models.Person;
import com.securityApp.repositories.PeopleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminServices {

    private final PeopleRepository peopleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminServices(PeopleRepository peopleRepository, PasswordEncoder passwordEncoder) {
        this.peopleRepository = peopleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Person> findAll() {
        return peopleRepository.findAll();
    }

    public Person findById(Integer id) {
        Optional<Person> person = peopleRepository.findById(id);
        return person.orElse(null);
    }

    public void savePerson(Person person) {
        Optional<Person> existingPerson = peopleRepository.findByUsername(person.getUsername());
        if (existingPerson.isPresent() && existingPerson.get().getId() != person.getId()) {
            throw new IllegalArgumentException("Username already exists");
        }
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        peopleRepository.save(person);
    }

    public void deleteById(Integer id) {
        peopleRepository.deleteById(id);
    }
}
