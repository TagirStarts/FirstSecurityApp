package com.securityApp.controllers;

import com.securityApp.models.Person;
import com.securityApp.models.Role;
import com.securityApp.services.AdminServicesImpl;
import com.securityApp.services.RoleServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminServicesImpl adminServicesImpl;
    private final RoleServiceImpl roleServiceImpl;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminController(AdminServicesImpl adminServicesImpl, RoleServiceImpl roleServiceImpl, PasswordEncoder passwordEncoder) {
        this.adminServicesImpl = adminServicesImpl;
        this.roleServiceImpl = roleServiceImpl;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String listPersons(Model model) {
        List<Person> persons = adminServicesImpl.findAll();
        model.addAttribute("persons", persons);
        return "admin/list";
    }

    @GetMapping("/create")
    public String createPersonForm(Model model) {
        model.addAttribute("person", new Person());
        model.addAttribute("roles", roleServiceImpl.findAll());
        return "admin/create";
    }

    @PostMapping("/save")
    public String savePerson(@ModelAttribute Person person, @RequestParam int roleId, Model model) {
        if (adminServicesImpl.usernameExists(person.getUsername())) {
            model.addAttribute("error", "Пользователь с таким именем уже существует");
            model.addAttribute("roles", roleServiceImpl.findAll());
            return "admin/create";
        }

        Role role = roleServiceImpl.findById(roleId);
        if (role != null) {
            person.getRoles().clear();
            person.getRoles().add(role);

            if (person.getPassword() != null && !person.getPassword().isEmpty()) {
                person.setPassword(passwordEncoder.encode(person.getPassword()));
            }

            try {
                adminServicesImpl.savePerson(person);
            } catch (Exception e) {
                model.addAttribute("error", "Ошибка при сохранении пользователя");
                return "admin/create";
            }
        } else {
            model.addAttribute("error", "Выбранная роль не найдена");
            return "admin/create";
        }

        return "redirect:/admin";
    }

    @GetMapping("/edit/{id}")
    public String editPersonForm(@PathVariable int id, Model model) {
        Person person = adminServicesImpl.findById(id);
        if (person == null) {
            return "redirect:/admin";
        }
        List<Role> roles = roleServiceImpl.findAll();
        model.addAttribute("person", person);
        model.addAttribute("roles", roles);
        return "admin/edit";
    }

    @PostMapping("/update/{id}")
    public String updatePerson(@PathVariable int id, @RequestParam int roleId, @ModelAttribute Person person, Model model) {
        Person existingPerson = adminServicesImpl.findById(id);

        if (existingPerson == null) {
            model.addAttribute("error", "Пользователь не найден");
            return "admin/edit";
        }

        if (!existingPerson.getUsername().equals(person.getUsername()) && adminServicesImpl.usernameExists(person.getUsername())) {
            model.addAttribute("error", "Пользователь с таким именем уже существует");
            model.addAttribute("roles", roleServiceImpl.findAll());
            return "admin/edit";
        }

        existingPerson.setUsername(person.getUsername());

        if (person.getPassword() != null && !person.getPassword().isEmpty()) {
            existingPerson.setPassword(passwordEncoder.encode(person.getPassword()));
        }

        existingPerson.getRoles().clear();
        Role role = roleServiceImpl.findById(roleId);
        if (role != null) {
            existingPerson.getRoles().add(role);
        } else {
            model.addAttribute("error", "Выбранная роль не найдена");
            return "admin/edit";
        }

        try {
            adminServicesImpl.savePerson(existingPerson);
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при сохранении пользователя");
            return "admin/edit";
        }

        return "redirect:/admin";
    }

    @GetMapping("/delete/{id}")
    public String deletePerson(@PathVariable int id) {
        adminServicesImpl.deleteById(id);
        return "redirect:/admin";
    }
}
