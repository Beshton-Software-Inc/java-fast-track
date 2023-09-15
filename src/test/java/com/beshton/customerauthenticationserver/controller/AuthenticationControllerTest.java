package com.beshton.customerauthenticationserver.controller;

import com.beshton.customerauthenticationserver.controller.AuthenticationController;
import com.beshton.customerauthenticationserver.model.ApplicationUser;
import com.beshton.customerauthenticationserver.model.LoginResponseDTO;
import com.beshton.customerauthenticationserver.model.PasswordDTO;
import com.beshton.customerauthenticationserver.model.RegistrationDTO;
import com.beshton.customerauthenticationserver.repository.PasswordResetTokenRepository;
import com.beshton.customerauthenticationserver.repository.RoleRepository;
import com.beshton.customerauthenticationserver.repository.UserRepository;
import com.beshton.customerauthenticationserver.service.AuthenticationService;
import com.beshton.customerauthenticationserver.service.CustomerUserDetailsService;
import com.beshton.customerauthenticationserver.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AuthenticationService authenticationService;
    @MockBean
    private CustomerUserDetailsService customerUserDetailsService;
    @MockBean
    private TokenService tokenService;
    @MockBean
    private RoleRepository roleRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private AuthenticationManager authenticationManager;

    @Autowired
    private ObjectMapper objectMapper;

    private RegistrationDTO registrationDTO;

    private PasswordDTO passwordDTO;

    private ApplicationUser applicationUser;
    private LoginResponseDTO loginResponseDTO;

    @BeforeEach
    public void init(){
       registrationDTO = RegistrationDTO.builder()
                        .username("test user3").email("testuser@gmail.com")
                        .password("1234").build();
        applicationUser = ApplicationUser.builder().username("test user3")
                                        .email("testuser@gmail.com")
                                        .password("1234").build();
       passwordDTO = PasswordDTO.builder().email("testuser@gmail.com")
                                        .newPassword("1111").build();
        loginResponseDTO = LoginResponseDTO.builder().user(applicationUser).jwt("some mock token").build();

    }
    @Test
    public void AuthenticationController_register_ReturnApplicationUser() throws Exception {
        given(authenticationService.registerUser(registrationDTO.getUsername(), registrationDTO.getEmail(),registrationDTO.getPassword()))
        .willReturn(applicationUser);
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", CoreMatchers.is(applicationUser.getUsername())));
    }

    @Test
    public void AuthenticationController_loginUser_whenValidInputIsGiven() throws Exception{
        //Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(registrationDTO.getEmail(), registrationDTO.getPassword()));
        Authentication auth = new TestingAuthenticationToken(registrationDTO.getEmail(), registrationDTO.getPassword());

        //mock authentication
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        //mock the repository and token service calls authenticationService.loginUser
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(applicationUser));
        when(tokenService.generateJwt(any())).thenReturn("some mock token");
        //mock authenticationService.loginUser calls in controller
        when(authenticationService.loginUser(applicationUser.getEmail(), applicationUser.getPassword())).thenReturn(loginResponseDTO);

      mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt", CoreMatchers.is(loginResponseDTO.getJwt())));
//        System.out.println(result.getResponse().getContentAsString() +"PRINT RESPONSE");

        verify(authenticationService, times(1)).loginUser(registrationDTO.getEmail(), registrationDTO.getPassword());

    }





}
