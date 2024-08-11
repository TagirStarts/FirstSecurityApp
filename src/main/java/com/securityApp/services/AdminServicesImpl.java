package com.securityApp.services;

import com.securityApp.models.Person;
import com.securityApp.repositories.PeopleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class AdminServicesImpl {

    private final PeopleRepository peopleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminServicesImpl(PeopleRepository peopleRepository, PasswordEncoder passwordEncoder) {
        this.peopleRepository = peopleRepository;
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
    public void savePerson(Person person) {
        Optional<Person> existingPerson = peopleRepository.findByUsername(person.getUsername());

        if (existingPerson.isPresent() && existingPerson.get().getId() != person.getId()) {
            throw new IllegalArgumentException("Пользователь с таким именем уже существует");
        }

        if (existingPerson.isEmpty() || !passwordEncoder.matches(person.getPassword(), existingPerson.get().getPassword())) {
            person.setPassword(passwordEncoder.encode(person.getPassword()));
        } else {
            person.setPassword(existingPerson.get().getPassword()); // Используем существующий зашифрованный пароль
        }

        peopleRepository.save(person);
    }

    @Transactional
    public void deleteById(int id) {
        peopleRepository.deleteById(id);
    }

    public boolean usernameExists(String username) {
        return peopleRepository.findByUsername(username).isPresent();
    }
}
