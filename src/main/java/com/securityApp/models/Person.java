package com.securityApp.models;

import jakarta.persistence.*;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
@Entity
@Table (name = "Person")
public class Person {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "id")
    private int id;
    @Column (name = "username")
    @NotEmpty
    @Size(min = 2, max = 20, message = "more than 2 and less than 20")  
    private String username;
    @Column (name = "password")
    @NotEmpty
    @Size(min = 2, max = 20, message = "more than 2 and less than 20")
    private String password;
    public Person() {};
    public Person(String username,  String password) {
        this.username = username;
        this.password = password;

    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
