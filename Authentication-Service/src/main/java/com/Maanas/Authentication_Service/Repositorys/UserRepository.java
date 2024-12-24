package com.Maanas.Authentication_Service.Repositorys;



import com.Maanas.Authentication_Service.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Find user by username
    Optional<User> findByUsername(String username);

    // Check if a user exists by username
    boolean existsByUsername(String username);

    // Find user by refresh token
    Optional<User> findByRefreshToken(String refreshToken);
}
