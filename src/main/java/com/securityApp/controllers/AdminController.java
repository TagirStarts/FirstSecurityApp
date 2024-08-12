package com.securityApp.controllers;

import com.securityApp.models.Person;
import com.securityApp.models.Role;
import com.securityApp.repositories.PeopleRepository;
import com.securityApp.services.AdminServicesImpl;
import com.securityApp.services.RoleServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminServicesImpl adminServicesImpl;
    private final RoleServiceImpl roleServiceImpl;
    private final PasswordEncoder passwordEncoder;
    private final PeopleRepository peopleRepository;

    @Autowired
    public AdminController(AdminServicesImpl adminServicesImpl, RoleServiceImpl roleServiceImpl, PasswordEncoder passwordEncoder, PeopleRepository peopleRepository) {
        this.adminServicesImpl = adminServicesImpl;
        this.roleServiceImpl = roleServiceImpl;
        this.passwordEncoder = passwordEncoder;
        this.peopleRepository = peopleRepository;
    }

    @GetMapping
    public String listPersons(Model model) {
        List<Person> persons = adminServicesImpl.findAll();
        List<Role> roles = roleServiceImpl.findAll(); // Получите все роли для формы, если нужно

        // Получите текущего пользователя и его роли
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();

            Optional<Person> optionalPerson = peopleRepository.findByUsername(username);
            if (optionalPerson.isPresent()) {
                Person currentUser = optionalPerson.get();
                Set<String> userRoles = currentUser.getRoles().stream()
                        .map(Role::getName) // Предполагается, что у роли есть метод getName()
                        .collect(Collectors.toSet());

                model.addAttribute("username", username);
                model.addAttribute("userRole", userRoles.isEmpty() ? "No Role" : userRoles.iterator().next()); // Передаем первую роль
            } else {
                model.addAttribute("username", "Unknown");
                model.addAttribute("userRole", "No Role");
            }
        }

        model.addAttribute("persons", persons);
        model.addAttribute("person", new Person()); // Для формы создания
        model.addAttribute("roles", roles); // Роли для формы создания

        return "admin/list";
    }

    @PostMapping("/save")
    public String savePerson(@ModelAttribute Person person, @RequestParam int roleId, Model model) {
        if (adminServicesImpl.usernameExists(person.getUsername())) {
            model.addAttribute("persons", adminServicesImpl.findAll());
            model.addAttribute("roles", roleServiceImpl.findAll());
            model.addAttribute("person", person);
            model.addAttribute("error", "Пользователь с таким именем уже существует");
            return "admin/list";
        }

        Role role = roleServiceImpl.findById(roleId);
        if (role != null) {
            person.getRoles().clear();
            person.getRoles().add(role);

            try {
                adminServicesImpl.savePerson(person);
            } catch (Exception e) {
                model.addAttribute("persons", adminServicesImpl.findAll());
                model.addAttribute("roles", roleServiceImpl.findAll());
                model.addAttribute("person", person);
                model.addAttribute("error", "Ошибка при сохранении пользователя");
                return "admin/list";
            }
        } else {
            model.addAttribute("persons", adminServicesImpl.findAll());
            model.addAttribute("roles", roleServiceImpl.findAll());
            model.addAttribute("person", person);
            model.addAttribute("error", "Выбранная роль не найдена");
            return "admin/list";
        }

        return "redirect:/admin";
    }

    @PostMapping("/update/{id}")
    public String updatePerson(@PathVariable int id, @RequestParam int roleId, @ModelAttribute Person person, Model model) {
        Person existingPerson = adminServicesImpl.findById(id);

        if (existingPerson == null) {
            model.addAttribute("persons", adminServicesImpl.findAll());
            model.addAttribute("roles", roleServiceImpl.findAll());
            model.addAttribute("error", "Пользователь не найден");
            return "admin/list";
        }

        if (!existingPerson.getUsername().equals(person.getUsername()) && adminServicesImpl.usernameExists(person.getUsername())) {
            model.addAttribute("persons", adminServicesImpl.findAll());
            model.addAttribute("roles", roleServiceImpl.findAll());
            model.addAttribute("error", "Пользователь с таким именем уже существует");
            return "admin/list";
        }

        existingPerson.setUsername(person.getUsername());
        existingPerson.setEmail(person.getEmail());
        existingPerson.setAge(person.getAge());

        if (person.getPassword() != null && !person.getPassword().isEmpty()) {
            existingPerson.setPassword(passwordEncoder.encode(person.getPassword()));
        }

        existingPerson.getRoles().clear();
        Role role = roleServiceImpl.findById(roleId);
        if (role != null) {
            existingPerson.getRoles().add(role);
        } else {
            model.addAttribute("persons", adminServicesImpl.findAll());
            model.addAttribute("roles", roleServiceImpl.findAll());
            model.addAttribute("error", "Выбранная роль не найдена");
            return "admin/list";
        }

        try {
            adminServicesImpl.savePerson(existingPerson);
        } catch (Exception e) {
            model.addAttribute("persons", adminServicesImpl.findAll());
            model.addAttribute("roles", roleServiceImpl.findAll());
            model.addAttribute("error", "Ошибка при сохранении пользователя");
            return "admin/list";
        }

        return "redirect:/admin";
    }


    @GetMapping("/delete/{id}")
    public String deletePerson(@PathVariable int id) {
        adminServicesImpl.deleteById(id);
        return "redirect:/admin";
    }
}
