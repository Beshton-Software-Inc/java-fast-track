package com.beshton.customerauthenticationserver.repository;

import com.beshton.customerauthenticationserver.model.ApplicationUser;
import com.beshton.customerauthenticationserver.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;


@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Test
    public void UserRepository_findByUsername_ReturnRegisteredUser(){
        //arrange
        ApplicationUser  user = ApplicationUser.builder()
                .username("test user1").email("test@gmail.com").password("").build();
        //act
        ApplicationUser savedUser = userRepository.save(user);
        Optional<ApplicationUser> foundUserOpt = userRepository.findByUsername(savedUser.getUsername());
        //assert
        foundUserOpt.ifPresent(applicationUser ->Assertions.assertThat(applicationUser).isNotNull());
    }
}
