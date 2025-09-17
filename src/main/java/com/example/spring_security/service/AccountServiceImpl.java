package com.example.spring_security.service;

import com.example.spring_security.entities.AppRole;
import com.example.spring_security.entities.AppUser;
import com.example.spring_security.repositories.AppRoleRepository;
import com.example.spring_security.repositories.AppUserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@Slf4j
public class AccountServiceImpl implements AccountService {
    private AppUserRepository appUserRepository;
    private AppRoleRepository appRoleRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public AccountServiceImpl(
            AppUserRepository appUserRepository,
            AppRoleRepository appRoleRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder
    ) {
        this.appUserRepository = appUserRepository;
        this.appRoleRepository = appRoleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public AppUser saveUser(String username, String password, String confirmedPassword) {
        log.info("Saving user with username : {}", username);
        AppUser user = appUserRepository.findByUsername(username);

        if (user != null) throw new RuntimeException("User already exists");
        if (!password.equals(confirmedPassword)) throw new RuntimeException("Please confirm your password");

        AppUser appUser = new AppUser();
        appUser.setUsername(username);
        appUser.setActived(true);
        appUser.setPassword(bCryptPasswordEncoder.encode(password));
        appUserRepository.save(appUser);
        addRoleToUser(username,"USER");

        return appUser;
    }

    @Override
    public AppRole save(AppRole role) {
        log.info("Saving role : {}", role.getRoleName());
        return appRoleRepository.save(role);
    }

    @Override
    public AppUser loadUserByUsername(String username) {
        log.info("Load user with username : {}", username);
        return appUserRepository.findByUsername(username);
    }

    @Override
    public void addRoleToUser(String username, String rolename) {
        log.info("Adding role : {} to user : {}", rolename, username);
        AppUser appUser = appUserRepository.findByUsername(username);
        AppRole appRole = appRoleRepository.findByRoleName(rolename);
        appUser.getRoles().add(appRole);
    }

    @Override
    public List<AppUser> getUserList() {
        log.info("Fetch all users");
        return appUserRepository.findAll();
    }
}
