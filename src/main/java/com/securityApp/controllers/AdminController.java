package com.securityApp.controllers;

import com.securityApp.models.Person;
import com.securityApp.models.Role;
import com.securityApp.services.AdminServices;
import com.securityApp.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminServices adminServices;
    private final RoleService roleService;

    @Autowired
    public AdminController(AdminServices adminServices, RoleService roleService) {
        this.adminServices = adminServices;
        this.roleService = roleService;
    }

    @GetMapping
    public String adminHomePage() {
        return "admin/home";
    }

    @GetMapping("/persons")
    public String listPersons(Model model) {
        List<Person> persons = adminServices.findAll();
        model.addAttribute("persons", persons);
        return "admin/list";
    }

    @GetMapping("/persons/{id}")
    public String viewPerson(@PathVariable Integer id, Model model) {
        Person person = adminServices.findById(id);
        if (person == null) {
            model.addAttribute("error", "Person not found");
            return "admin/home";
        }
        model.addAttribute("person", person);
        return "admin/view";
    }

    @GetMapping("/persons/edit/{id}")
    public String editPersonForm(@PathVariable Integer id, Model model) {
        Person person = adminServices.findById(id);
        if (person == null) {
            return "redirect:/admin/persons";
        }
        List<Role> roles = roleService.findAll();
        model.addAttribute("person", person);
        model.addAttribute("roles", roles);
        return "admin/edit";
    }

    @PostMapping("/persons")
    public String savePerson(@ModelAttribute("person") Person person) {
        adminServices.savePerson(person);
        return "redirect:/admin/persons";
    }

    @PostMapping("/persons/update/{id}")
    public String updatePerson(@PathVariable Integer id, @RequestParam List<Integer> roleIds) {
        Person person = adminServices.findById(id);
        if (person != null) {
            person.getRoles().clear();
            for (Integer roleId : roleIds) {
                Role role = roleService.findById(roleId);
                if (role != null) {
                    person.getRoles().add(role);
                }
            }
            adminServices.savePerson(person);
        }
        return "redirect:/admin/persons";
    }

    @GetMapping("/persons/delete/{id}")
    public String deletePerson(@PathVariable Integer id) {
        adminServices.deleteById(id);
        return "redirect:/admin/persons";
    }
}
