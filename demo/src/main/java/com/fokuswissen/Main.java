package com.fokuswissen;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.fokuswissen.user.User;
import com.fokuswissen.user.UserRepository;
import com.fokuswissen.user.UserRole;
import com.fokuswissen.user.UserService;

@SpringBootApplication
public class Main implements CommandLineRunner
{
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        String adminUsername = "admin";

        Optional<User> adminOpt = userRepository.findByUsername(adminUsername);

        if (adminOpt.isEmpty()) {
            // Admin existiert nicht, also anlegen
            userService.register(adminUsername, "admin@admin.com", "securePassword");

            // Admin User holen, Rollen & enabled setzen
            User admin = userRepository.findByUsername(adminUsername).get();
            admin.getRoles().add(UserRole.ROLE_ADMIN);
            admin.setEnabled(true);
            userRepository.save(admin);

            System.out.println("Admin user created");
        } else {
            System.out.println("Admin user already exists");
        }
    }
}
