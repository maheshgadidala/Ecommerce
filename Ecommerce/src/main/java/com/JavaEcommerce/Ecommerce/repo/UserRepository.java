package com.JavaEcommerce.Ecommerce.repo;

import com.JavaEcommerce.Ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String userName);

    boolean existsByUserName(String username);

    boolean existsByUserEmail(String email);
}
