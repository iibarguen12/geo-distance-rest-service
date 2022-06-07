package com.wcc.geographicdistance;

import com.wcc.geographicdistance.domain.Role;
import com.wcc.geographicdistance.domain.Roles;
import com.wcc.geographicdistance.domain.User;
import com.wcc.geographicdistance.service.PostcodeService;
import com.wcc.geographicdistance.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;


@SpringBootApplication
public class GeographicDistanceApplication {

	public static void main(String[] args) {

		SpringApplication.run(GeographicDistanceApplication.class, args);
	}

	@Bean
	BCryptPasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}

	@Bean
	CommandLineRunner run(UserService userService, PostcodeService postcodeService){
		return args -> {
			//Add roles
			userService.saveRole(new Role(null,Roles.ROLE_USER.name()));
			userService.saveRole(new Role(null,Roles.ROLE_ADMIN.name()));

			//Add users
			userService.saveUser(new User(null, "WCC User","wcc_user","user",new ArrayList<>()));
			userService.saveUser(new User(null, "WCC Admin","wcc_admin","admin",new ArrayList<>()));

			//Add roles to users
			userService.addRoleToUser("wcc_user", Roles.ROLE_USER.name());
			userService.addRoleToUser("wcc_admin", Roles.ROLE_ADMIN.name());

			//Add 100 first postcodes from the CSV file
			postcodeService.load100FirstPostcodesFromCSV();
		};
	}
}
