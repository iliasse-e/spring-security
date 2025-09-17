package com.example.spring_security;

import com.example.spring_security.entities.AppRole;
import com.example.spring_security.service.AccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringSecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityApplication.class, args);
	}

	@Bean
	CommandLineRunner start(AccountService accountService){
		return args->{
			accountService.save(new AppRole(null, "USER"));
			accountService.save(new AppRole(null, "ADMIN"));
			accountService.save(new AppRole(null, "MANAGER"));

			accountService.saveUser("Mohammed", "JeSuisMalade", "JeSuisMalade");
			accountService.saveUser("Raj", "JeSuisMalade", "JeSuisMalade");
			accountService.saveUser("Xu", "JeSuisMalade", "JeSuisMalade");

			accountService.addRoleToUser("Raj", "ADMIN");
			accountService.addRoleToUser("Xu", "MANAGER");

		};
	}

}
