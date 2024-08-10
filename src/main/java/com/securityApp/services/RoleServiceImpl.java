package com.securityApp.services;

import com.securityApp.models.Role;
import com.securityApp.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
    @Transactional
    public List<Role> findAll() {
        return roleRepository.findAll();
    }
    @Transactional
    public Role findById(Integer id) {
        Optional<Role> role = roleRepository.findById(id);
        return role.orElse(null);
    }
}
