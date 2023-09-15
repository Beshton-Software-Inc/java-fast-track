package com.beshton.customerauthenticationserver.service;

import com.beshton.customerauthenticationserver.model.ApplicationUser;
import com.beshton.customerauthenticationserver.model.LoginResponseDTO;
import com.beshton.customerauthenticationserver.model.Role;
import com.beshton.customerauthenticationserver.repository.RoleRepository;
import com.beshton.customerauthenticationserver.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
@Slf4j
@Service
@Transactional
public class AuthenticationService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenService tokenService;


    public ApplicationUser registerUser(String username, String email, String password){
        String encodedPassword = passwordEncoder.encode(password);
        Role userRole = roleRepository.findByAuthority("CUSTOMER").get();
        Set<Role> authorities = new HashSet<>();
        authorities.add(userRole);
        return userRepository.save(new ApplicationUser(0, username, email, encodedPassword, authorities));
    }
    public LoginResponseDTO loginUser(String email, String password){

        try{
            String username = "";
            if(userRepository.findByEmail(email).isPresent()) {
                username = userRepository.findByEmail(email).get().getUsername();
            }
                Authentication auth = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(username, password)
                );
                System.out.println("in auth-service, loginUser()" + username + auth);

                String token = tokenService.generateJwt(auth);
                return new LoginResponseDTO(userRepository.findByEmail(email).get(), token);

        } catch(AuthenticationException e){
            log.error("authentication fail", e);
            throw new RuntimeException("Authentication failed for email " + email);
            //return new LoginResponseDTO(null, "");
        }
    }


}
