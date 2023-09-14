package com.beshton.customerauthenticationserver.service;

import com.beshton.customerauthenticationserver.model.ApplicationUser;
import com.beshton.customerauthenticationserver.repository.PasswordResetTokenRepository;
import com.beshton.customerauthenticationserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerUserDetailsService implements UserDetailsService {
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("in the user detail service -load user by username");
        //System.out.println(userRepository.findByEmail(email));
        return userRepository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("not found this user"));
//        if(!username.equals("yh")) throw new UsernameNotFoundException("not yh");
//        Set<Role> roles = new HashSet<>();
//        roles.add(new Role(1, "USER"));
//        return new ApplicationUser(1, "yh", encoder.encode("1234"), roles);
    }

    public Optional<ApplicationUser> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    public Optional<ApplicationUser> getUserByPasswordResetToken(String token) {
        return Optional.ofNullable(passwordResetTokenRepository.findByToken(token).getApplicationUser());
    }

    public void changePassword(ApplicationUser applicationUser, String newPassword) {
        System.out.println("in change password service"+newPassword);
        applicationUser.setPassword(encoder.encode(newPassword));
        userRepository.save(applicationUser);
    }
}
