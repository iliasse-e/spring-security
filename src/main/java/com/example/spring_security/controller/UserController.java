package com.example.spring_security.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.spring_security.entities.AppRole;
import com.example.spring_security.entities.AppUser;
import com.example.spring_security.service.AccountService;
import com.example.spring_security.utils.RoleToUserForm;
import com.example.spring_security.utils.UserForm;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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

    @GetMapping("/refreshToken")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authToken = request.getHeader("Authorization");

        if (authToken != null && authToken.startsWith("Bearer ")) {
            try {
                String jwtRefreshToken = authToken.substring(7);
                Algorithm algorithm = Algorithm.HMAC256("mySecret1234"); // On utilise la même clé pour crypter et décrypter
                JWTVerifier jwtVerifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = jwtVerifier.verify(jwtRefreshToken);
                String username = decodedJWT.getSubject();

                AppUser appUser = accountService.loadUserByUsername(username);

                // Créé un access token
                String jwtAccessToken = JWT.create()
                        .withSubject(appUser.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 1 * 60 * 1000))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("roles", appUser.getRoles().stream().map(role -> role.getRoleName()).collect(Collectors.toList()))
                        .sign(algorithm);

                Map<String, String> idToken = new HashMap<>();
                idToken.put("access-token", jwtAccessToken);
                idToken.put("refresh-token", jwtRefreshToken);
                response.setContentType("application/json");
                new ObjectMapper().writeValue(response.getOutputStream(), idToken); // envoie idToken dans le body en format JSON

            } catch (Exception e) {
                throw e;
            }

        } else {
            throw new RuntimeException("Refresh token required");
        }
    }
}
