package com.beshton.customerauthenticationserver.service;


import com.beshton.customerauthenticationserver.model.ApplicationUser;
import com.beshton.customerauthenticationserver.model.PasswordResetToken;
import com.beshton.customerauthenticationserver.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Calendar;
import java.util.stream.Collectors;

@Service
public class TokenService {

    @Autowired
    private JwtEncoder jwtEncoder;
    @Autowired
    private JwtDecoder jwtDecoder;
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    public String generateJwt(Authentication auth){
        Instant now = Instant.now();

        String scope = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .subject(auth.getName())
                .claim("roles", scope)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }


    public String createPasswordResetTokenForUser(ApplicationUser applicationUser, String token) {

        if(passwordResetTokenRepository.findByApplicationUser(applicationUser).isPresent()){
            passwordResetTokenRepository.delete(passwordResetTokenRepository.findByApplicationUser(applicationUser).get());

        }
        PasswordResetToken passwordResetToken = new PasswordResetToken(applicationUser, token);
        passwordResetTokenRepository.save(passwordResetToken);
        return "created new token";
    }

    public String validatePasswordResetToken(String token) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);
        if(passwordResetToken == null){
            return "invalid";
        }

        Calendar cal = Calendar.getInstance();
        if((passwordResetToken.getExpirationTime().getTime()- cal.getTime().getTime())<=0){
            passwordResetTokenRepository.delete(passwordResetToken);
            return "expired";
        }


        return "valid";
    }
}
