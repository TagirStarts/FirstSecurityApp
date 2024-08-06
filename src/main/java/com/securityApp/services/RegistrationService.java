package com.securityApp.services;

import com.securityApp.models.Person;
import com.securityApp.repositories.PeopleRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationService {
    private final PeopleRepository peopleRepository;
    @Autowired
    public RegistrationService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }
    @Transactional
    public void registerPeople(Person person) {
        System.out.println("Registering person: " + person);
        peopleRepository.save(person);
    }

}
