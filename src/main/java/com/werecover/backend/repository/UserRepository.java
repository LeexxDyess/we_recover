package com.werecover.backend.repository;

import com.werecover.backend.model.Role;
import com.werecover.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    // ✅ Find all users with a specific role (e.g., "ROLE_SPONSEE")
    List<User> findByRole(Role role); // ✅ Use Role enum instead of String
}
