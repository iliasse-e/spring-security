package com.example.spring_security.controller;

import com.example.spring_security.entities.AppUser;
import com.example.spring_security.service.AccountService;
import com.example.spring_security.utils.UserForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {
    @Autowired
    private AccountService accountService;
    @PostMapping("/register")
    public AppUser register(@RequestBody UserForm userForm){
        return  accountService.saveUser(
                userForm.getUsername(),
                userForm.getPassword(),
                userForm.getConfirmedPassword()
        );
    }

    @GetMapping("/users")
    public List<AppUser> userList() {
        return accountService.getUserList();
    }
}
