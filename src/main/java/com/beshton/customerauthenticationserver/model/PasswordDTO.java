package com.beshton.customerauthenticationserver.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordDTO {
    private String email;
    private String oldPassword;
    private String newPassword;

}
