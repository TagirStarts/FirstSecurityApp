package com.securityApp.controllers;

import com.securityApp.models.Person;
import com.securityApp.models.Role;
import com.securityApp.services.AdminServicesImpl;
import com.securityApp.services.RoleServiceImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class UserController {
    private final AdminServicesImpl adminServicesImpl;
    private final RoleServiceImpl roleServiceImpl;

    public UserController(final AdminServicesImpl adminServicesImpl, RoleServiceImpl roleServiceImpl) {
        this.adminServicesImpl = adminServicesImpl;
        this.roleServiceImpl = roleServiceImpl;
    }

    @GetMapping("/user")
    public String userHomePage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();

            Optional<Person> optionalPerson = adminServicesImpl.findByUsername(username);
            if (optionalPerson.isPresent()) {
                Person currentUser = optionalPerson.get();

                // Добавляем данные пользователя в модель
                model.addAttribute("person", currentUser);

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

        return "user/home"; // Исправлено на правильное имя шаблона
    }
}