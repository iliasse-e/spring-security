package com.example.spring_security.service;

import com.example.spring_security.entities.AppRole;
import com.example.spring_security.entities.AppUser;

import java.util.List;

public interface AccountService {
    public AppUser saveUser(String username, String password, String confirmedPassword);
    public AppRole saveRole(AppRole role);
    public AppUser loadUserByUsername(String username);
    public void addRoleToUser(String username, String rolename);

    List<AppUser> getUserList();
}
