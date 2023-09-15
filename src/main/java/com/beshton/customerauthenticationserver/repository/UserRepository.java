package com.beshton.customerauthenticationserver.repository;

import com.beshton.customerauthenticationserver.model.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<ApplicationUser, Integer>  {
    Optional<ApplicationUser> findByUsername(String username);
    Optional<ApplicationUser> findByEmail(String email);
}
