package com.securityApp.util;

import com.securityApp.models.Person;
import com.securityApp.services.PersonDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class PersonValidator implements Validator {
    private final PersonDetailServiceImpl personDetailServiceImpl;

    @Autowired
    public PersonValidator(PersonDetailServiceImpl personDetailServiceImpl) {
        this.personDetailServiceImpl = personDetailServiceImpl;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Person person = (Person) target;
        try {
            personDetailServiceImpl.loadUserByUsername(person.getUsername());
        } catch (UsernameNotFoundException ignored) {
            return;
        }
        errors.rejectValue("username", "", "Username already taken");
    }
}
