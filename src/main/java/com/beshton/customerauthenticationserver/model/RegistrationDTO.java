package com.beshton.customerauthenticationserver.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDTO {

    private String username;
    private String email;
    private String password;
    private String matchingPassword;

    @Override
    public String toString() {
        return "RegistrationDTO{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", matchingPassword='" + matchingPassword + '\'' +
                '}';
    }


}
