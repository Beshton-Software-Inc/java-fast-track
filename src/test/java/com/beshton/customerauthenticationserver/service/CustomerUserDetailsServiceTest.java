package com.beshton.customerauthenticationserver.service;

import com.beshton.customerauthenticationserver.model.ApplicationUser;
import com.beshton.customerauthenticationserver.model.PasswordDTO;
import com.beshton.customerauthenticationserver.repository.PasswordResetTokenRepository;
import com.beshton.customerauthenticationserver.repository.UserRepository;
import com.beshton.customerauthenticationserver.service.CustomerUserDetailsService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomerUserDetailsServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock
    private PasswordEncoder encoder;
    @InjectMocks
    private CustomerUserDetailsService customerUserDetailsService;
    @Test
    public void CustomerService_loadUserByUsername_ReturnsUserDetails(){
        ApplicationUser testUser = ApplicationUser.builder()
                .username("test user2").email("testuser2@gmail.com").password("1234")
                .build();
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));

        UserDetails testUserDetails = customerUserDetailsService.loadUserByUsername(testUser.getUsername());
        Assertions.assertThat(testUserDetails).isNotNull();
    }
    @Test
    public void CustomerService_changePassword_NewPassword(){
        ApplicationUser testUser = ApplicationUser.builder()
                .username("test user3").email("testuser2@gmail.com").password("1234")
                .build();
        String newPassword = "1111";
        PasswordDTO passwordDTO = PasswordDTO.builder().newPassword(encoder.encode(newPassword)).build();
        when(userRepository.save(testUser)).thenReturn(testUser);

        customerUserDetailsService.changePassword(testUser, passwordDTO.getNewPassword());
        String changedPassword = testUser.getPassword();
        Assertions.assertThat(changedPassword).isEqualTo(passwordDTO.getNewPassword());
    }
}
