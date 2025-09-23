package com.example.spring_security.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.spring_security.entities.AppRole;
import com.example.spring_security.entities.AppUser;
import com.example.spring_security.service.AccountService;
import com.example.spring_security.utils.JWTUtil;
import com.example.spring_security.utils.RoleToUserForm;
import com.example.spring_security.utils.UserForm;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@Slf4j
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
        log.info("Refresh token processing");

        // 1. Récupération sécurisée du cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                log.info("Cookie reçu : {} = {}", cookie.getName(), cookie.getValue());
            }
        } else {
            throw new RuntimeException("Refresh token required");
        }

        String refreshToken = Optional.ofNullable(request.getCookies())
                .map(Arrays::stream)
                .orElseGet(Stream::empty)
                .filter(c -> "refresh-token".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        log.info("Found refresh token with value : {}", refreshToken);

        if (refreshToken != null) {
            try {
                // 2. Vérification du token
                log.info("Refresh token verification processing");
                Algorithm algorithm = Algorithm.HMAC256(JWTUtil.SECRET); // On utilise la même clé pour crypter et décrypter
                JWTVerifier jwtVerifier = JWT.require(algorithm).build();

                log.info("Refresh token decoding processing");
                DecodedJWT decodedJWT = jwtVerifier.verify(refreshToken);
                log.info("decoded");
                String username = decodedJWT.getSubject();
                log.info("Refresh token valid for user: {}", username);

                AppUser appUser = accountService.loadUserByUsername(username);

                // 3. Génération d’un nouveau access token
                String jwtAccessToken = JWT.create()
                        .withSubject(appUser.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis() + JWTUtil.EXPIRE_ACCESS_TOKEN))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("roles", appUser.getRoles().stream().map(role -> role.getRoleName()).collect(Collectors.toList()))
                        .sign(algorithm);

                Map<String, String> idToken = new HashMap<>();
                idToken.put("access-token", jwtAccessToken);
                response.setContentType("application/json");
                new ObjectMapper().writeValue(response.getOutputStream(), idToken); // envoie idToken dans le body en format JSON

            } catch (Exception e) {
                throw e;
            }

        } else {
            throw new RuntimeException("Refresh token required");
        }
    }

    @GetMapping("/profile")
    public AppUser profile(Principal principal) {
        return accountService.loadUserByUsername(principal.getName());
    }
}
