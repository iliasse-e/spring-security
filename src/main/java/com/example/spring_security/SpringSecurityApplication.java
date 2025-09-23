package com.example.spring_security;

import com.example.spring_security.entities.AppRole;
import com.example.spring_security.service.AccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SpringSecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityApplication.class, args);
	}

	@Bean
	CommandLineRunner start(AccountService accountService){
		return args->{
			accountService.saveRole(new AppRole(null, "USER"));
			accountService.saveRole(new AppRole(null, "ADMIN"));
			accountService.saveRole(new AppRole(null, "MANAGER"));

			accountService.saveUser("Mohammed", "1234", "1234");
			accountService.saveUser("Raj", "1234", "1234");
			accountService.saveUser("Xu", "1234", "1234");

			accountService.addRoleToUser("Raj", "ADMIN");
			accountService.addRoleToUser("Xu", "MANAGER");

		};
	}

}
