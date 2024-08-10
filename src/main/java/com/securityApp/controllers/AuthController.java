package com.securityApp.controllers;

import com.securityApp.models.Person;
import com.securityApp.services.RegistrationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;

@Controller
public class AuthController {

    private final RegistrationServiceImpl registrationServiceImpl;

    @Autowired
    public AuthController(RegistrationServiceImpl registrationServiceImpl) {
        this.registrationServiceImpl = registrationServiceImpl;
    }

    @GetMapping("/auth/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/auth/registration")
    public String registrationPage(Model model) {
        model.addAttribute("person", new Person());
        return "auth/registration";
    }

    @PostMapping("/auth/registration")
    public String performRegistration(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "auth/registration";
        }
        try {
            registrationServiceImpl.register(person);
            return "redirect:/auth/login";
        } catch ( IllegalArgumentException e ) {
            model.addAttribute("error", e.getMessage()); // Добавляем сообщение об ошибке в модель
            return "auth/registration";
        }

    }
}
