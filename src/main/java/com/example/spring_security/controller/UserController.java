package com.example.spring_security.controller;

import com.example.spring_security.entities.AppRole;
import com.example.spring_security.entities.AppUser;
import com.example.spring_security.service.AccountService;
import com.example.spring_security.utils.RoleToUserForm;
import com.example.spring_security.utils.UserForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {
    @Autowired
    private AccountService accountService;

    @PostMapping("/users")
    @PostAuthorize("hasAuthority('ADMIN')")
    public AppUser register(@RequestBody UserForm userForm){
        return  accountService.saveUser(
                userForm.getUsername(),
                userForm.getPassword(),
                userForm.getConfirmedPassword()
        );
    }

    @GetMapping("/users")
    @PostAuthorize("hasAuthority('USER')")
    public List<AppUser> userList() {
        return accountService.getUserList();
    }

    @PostMapping("/roles")
    @PostAuthorize("hasAuthority('USER')")
    public AppRole saveRole(@RequestBody AppRole appRole){
        return accountService.saveRole(appRole);
    }

    @PostMapping("/roles/addToUser")
    @PostAuthorize("hasAuthority('USER')")
    public void addRoleToUser(@RequestBody RoleToUserForm roleToUserForm) {
        accountService.addRoleToUser(roleToUserForm.getUsername(), roleToUserForm.getRole());
    }
}
