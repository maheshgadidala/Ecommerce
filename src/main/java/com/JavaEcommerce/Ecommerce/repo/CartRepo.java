package com.JavaEcommerce.Ecommerce.repo;

import com.JavaEcommerce.Ecommerce.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CartRepo extends JpaRepository<Cart, Long> {
    // find cart by the user's email (Cart has a User relation named 'user' with field 'userEmail')
    Cart findByUserUserEmail(String email);
}
