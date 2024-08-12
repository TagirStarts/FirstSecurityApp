package com.securityApp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Entity
@Table(name = "Person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "username")
    @NotEmpty
    @Size(min = 2, max = 20, message = "more than 2 and less than 20")
    private String username;

    @Column(name = "firstname")
    @NotEmpty
    @Size(min = 2, max = 20, message = "more than 2 and less than 20")
    private String firstname;

    @Column(name = "lastname")
    @NotEmpty
    @Size(min = 2, max = 20, message = "more than 2 and less than 20")
    private String lastname;

    @Column(name = "password")
    @NotEmpty
    @Size(min = 2, max = 200, message = "more than 2 and less than 200")
    private String password;

    @Column (name = "age")
    private int age;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "person_roles",
            joinColumns = @JoinColumn(name = "person_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    public Person() {}

    public Person(String username, String firstname, int age, String lastname) {
        this.username = username;
        this.firstname = firstname;
        this.password = lastname;
        this.age = age;
    }

    public String getRolesAsString() {
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.joining(", "));
    }
}

