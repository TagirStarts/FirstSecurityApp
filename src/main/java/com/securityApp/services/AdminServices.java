package com.securityApp.services;

import com.securityApp.models.Person;
import com.securityApp.repositories.PeopleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminServices {

    private final PeopleRepository peopleRepository;

    @Autowired
    public AdminServices(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    public List<Person> findAll() {
        return peopleRepository.findAll();
    }

    public Person findById(Integer id) {
        Optional<Person> person = peopleRepository.findById(id);
        return person.orElse(null);
    }

    public void savePerson(Person person) {
        peopleRepository.save(person);
    }

    public void deleteById(Integer id) {
        peopleRepository.deleteById(id);
    }
}
