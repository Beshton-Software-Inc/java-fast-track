package com.beshton.customerauthenticationserver.controller;

import com.beshton.customerauthenticationserver.model.ApplicationUser;
import com.beshton.customerauthenticationserver.model.LoginResponseDTO;
import com.beshton.customerauthenticationserver.model.PasswordDTO;
import com.beshton.customerauthenticationserver.model.RegistrationDTO;
import com.beshton.customerauthenticationserver.service.AuthenticationService;
import com.beshton.customerauthenticationserver.service.CustomerUserDetailsService;
import com.beshton.customerauthenticationserver.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
@Slf4j
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private CustomerUserDetailsService customerUserDetailsService;
    @Autowired
    private TokenService tokenService;

    @PostMapping("/register")
    public ApplicationUser registerUser(@RequestBody RegistrationDTO detailsBody){
        System.out.println(detailsBody.toString());
        return authenticationService.registerUser(detailsBody.getUsername(), detailsBody.getEmail(), detailsBody.getPassword());
    }
    @PostMapping("/login")
    public LoginResponseDTO loginUser(@RequestBody RegistrationDTO body){
        LoginResponseDTO loginDTO = authenticationService.loginUser(body.getEmail(), body.getPassword());
        System.out.println(loginDTO.getUser());
        return loginDTO;
    }
    @PostMapping("/resetPassword")
    public String resetPassword(@RequestBody PasswordDTO passwordDTO, HttpServletRequest request){
        System.out.println(passwordDTO);
        Optional<ApplicationUser> userOpt = customerUserDetailsService.findUserByEmail(passwordDTO.getEmail());
        String url = "";
        if(userOpt.isPresent()){
            String token = UUID.randomUUID().toString();
            ApplicationUser applicationUser = userOpt.get();
            tokenService.createPasswordResetTokenForUser(applicationUser,token);
            url = passwordResetTokenMail(applicationUser, applicationUrl(request), token);
        }
        return url;
    }

    @PostMapping("/savePassword")
    public String savePassword(@RequestParam("token") String token, @RequestBody PasswordDTO passwordDTO){
        String result = tokenService.validatePasswordResetToken(token);
        if(!result.equalsIgnoreCase("valid")){
            return "invalid token";
        }
        Optional<ApplicationUser> applicationUser = customerUserDetailsService.getUserByPasswordResetToken(token);
        if(applicationUser.isPresent()){
            System.out.println(applicationUser + passwordDTO.getNewPassword());
            customerUserDetailsService.changePassword(applicationUser.get(), passwordDTO.getNewPassword());
            return "Password Reset Successfully";
        }else {
            return "Invalid Token";
        }
    }

    private String passwordResetTokenMail(ApplicationUser user, String applicationUrl, String token) {
        String url =
                applicationUrl
                        + "/auth/savePassword?token="
                        + token;

        //sendVerificationEmail()
        log.info("Click the link to Reset your Password: {}",
                url);
        return url;
    }

    private String applicationUrl(HttpServletRequest request) {
        return "http://" +
                request.getServerName() +
                ":" +
                request.getServerPort() +
                request.getContextPath();
    }
}
