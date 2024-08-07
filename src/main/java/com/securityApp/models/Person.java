package com.securityApp.models;

import jakarta.persistence.*;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
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
    @Size(min = 2, max = 2000, message = "more than 2 and less than 20")
    private String password;
    @Column (name="role")
    private String role;
    public Person() {};
    public Person(String username) {
        this.username = username;


    }



}
