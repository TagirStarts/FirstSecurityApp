package com.securityApp.services;

import com.securityApp.models.Person;

import java.util.List;

public interface AdminService {
    public List<Person> findAll();
    public Person findById(Integer id);
    public void savePerson(Person person);
    public void deleteById(Integer id);
}
