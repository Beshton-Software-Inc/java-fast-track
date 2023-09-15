package com.beshton.customerauthenticationserver;

import com.beshton.customerauthenticationserver.model.ApplicationUser;
import com.beshton.customerauthenticationserver.model.Role;
import com.beshton.customerauthenticationserver.repository.RoleRepository;
import com.beshton.customerauthenticationserver.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class CustomerAuthenticationServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerAuthenticationServerApplication.class, args);
    }
    @Bean
    CommandLineRunner run(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        System.out.println("run");
        return args -> {
            if (roleRepository.findByAuthority("ADMIN").isPresent()) {
                System.out.println("has admin");
                return;
            }
            Role adminRole = roleRepository.save(new Role("ADMIN"));
            roleRepository.save(new Role("CUSTOMER"));
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);

            ApplicationUser admin = new ApplicationUser(1, "admin", "admin@gmail.com", passwordEncoder.encode("admin"), roles);
            System.out.println(admin);
            userRepository.save(admin);
        };
    }

}
