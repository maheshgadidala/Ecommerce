package com.JavaEcommerce.Ecommerce.repo;

import com.JavaEcommerce.Ecommerce.model.Cart;
import com.JavaEcommerce.Ecommerce.model.CartsItem;
import com.JavaEcommerce.Ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface CartItemRepo extends JpaRepository<CartsItem, Long> {

    @Query("SELECT ci FROM CartsItem ci WHERE ci.cartId = ?1 AND ci.productId = ?2")
    CartsItem findCartItemByProductIdAndCartId(Long cartId, Long productId);
}
