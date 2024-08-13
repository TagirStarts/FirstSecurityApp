package com.securityApp.controllers;

import com.securityApp.models.Person;
import com.securityApp.models.Role;
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
import java.util.HashSet;

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
        List<Role> roles = roleServiceImpl.findAll();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();

            Optional<Person> optionalPerson = adminServicesImpl.findByUsername(username);
            if (optionalPerson.isPresent()) {
                Person currentUser = optionalPerson.get();
                Set<String> userRoles = currentUser.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet());

                model.addAttribute("username", username);
                model.addAttribute("userRole", userRoles.isEmpty() ? "No Role" : userRoles.iterator().next());
            } else {
                model.addAttribute("username", "Unknown");
                model.addAttribute("userRole", "No Role");
            }
        }

        model.addAttribute("persons", persons);
        model.addAttribute("roles", roles);

        return "admin/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        List<Role> roles = roleServiceImpl.findAll();
        model.addAttribute("person", new Person());
        model.addAttribute("roles", roles);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();

            Optional<Person> optionalPerson = adminServicesImpl.findByUsername(username);
            if (optionalPerson.isPresent()) {
                Person currentUser = optionalPerson.get();
                Set<String> userRoles = currentUser.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet());

                model.addAttribute("username", username);
                model.addAttribute("userRole", userRoles.isEmpty() ? "No Role" : userRoles.iterator().next());
            } else {
                model.addAttribute("username", "Unknown");
                model.addAttribute("userRole", "No Role");
            }
        }

        return "admin/create";
    }

    @PostMapping("/save")
    public String savePerson(@ModelAttribute Person person, @RequestParam List<Integer> roleIds, Model model) {
        if (adminServicesImpl.usernameExists(person.getUsername())) {
            model.addAttribute("roles", roleServiceImpl.findAll());
            model.addAttribute("person", person);
            model.addAttribute("error", "Пользователь с таким именем уже существует");
            return "admin/create";
        }

        try {
            person.setPassword(passwordEncoder.encode(person.getPassword()));
            adminServicesImpl.assignRolesToPerson(person, new HashSet<>(roleIds)); // Convert List to Set
        } catch (Exception e) {
            model.addAttribute("roles", roleServiceImpl.findAll());
            model.addAttribute("person", person);
            model.addAttribute("error", "Ошибка при сохранении пользователя");
            return "admin/create";
        }

        return "redirect:/admin";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model) {
        Person person = adminServicesImpl.findById(id);
        if (person == null) {
            return "redirect:/admin";
        }
        List<Role> roles = roleServiceImpl.findAll();
        model.addAttribute("person", person);
        model.addAttribute("roles", roles);
        return "admin/list";
    }

    @PostMapping("/update/{id}")
    public String updatePerson(@PathVariable int id, @RequestParam List<Integer> roleIds, @ModelAttribute Person person, Model model) {
        Person existingPerson = adminServicesImpl.findById(id);

        if (existingPerson == null) {
            return "redirect:/admin";
        }

        // Check for existing username conflict
        if (!existingPerson.getUsername().equals(person.getUsername()) && adminServicesImpl.usernameExists(person.getUsername())) {
            model.addAttribute("roles", roleServiceImpl.findAll());
            model.addAttribute("person", person);
            model.addAttribute("error", "Пользователь с таким именем уже существует");
            return "admin/list";
        }

        // Update non-sensitive fields
        existingPerson.setUsername(person.getUsername());
        existingPerson.setFirstname(person.getFirstname());
        existingPerson.setLastname(person.getLastname());
        existingPerson.setAge(person.getAge());



        // Update roles
        try {
            adminServicesImpl.assignRolesToPerson(existingPerson, new HashSet<>(roleIds)); // Convert List to Set
        } catch (Exception e) {
            model.addAttribute("roles", roleServiceImpl.findAll());
            model.addAttribute("person", existingPerson);
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
