package com.securityApp.controllers;

import com.securityApp.models.Person;
import com.securityApp.services.RegistrationService;
import com.securityApp.util.PersonValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final PersonValidator personValidator;
    private final RegistrationService registrationService;
    @Autowired
    public AuthController(PersonValidator personValidator, RegistrationService registrationService) {
        this.personValidator = personValidator;
        this.registrationService = registrationService;
    }

    @GetMapping( "/login")
    public String loginPage () {
        return "auth/login";
    }
    @GetMapping("/registration")
    public String registrationPage (@ModelAttribute("person") Person person) {
        return "auth/registration";
    }@PostMapping("/registration")
    public String performRegistration(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult) {
        System.out.println("before validation"+person);
        personValidator.validate(person, bindingResult);
        System.out.println("after validation"+person);
        if (bindingResult.hasErrors()) {
            System.out.println("binding error"+bindingResult.getAllErrors());
            return "auth/registration";
        }
        registrationService.registerPeople(person);
        return "redirect:/auth/login";
    }
}
