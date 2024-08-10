package com.securityApp.services;

import com.securityApp.models.Role;

import java.util.List;

public interface RoleService {
    public List<Role> findAll();
    public Role findById(Integer id);
}
