package com.securityApp.repositories;

import com.securityApp.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PeopleRepository extends JpaRepository<Person, Integer> {
    @Query("Select u from Person u left join fetch u.roles where u.username=:username")
    Optional<Person> findByUsername(String username);
}
