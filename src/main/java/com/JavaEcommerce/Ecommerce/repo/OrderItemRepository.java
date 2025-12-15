package com.JavaEcommerce.Ecommerce.repo;

import com.JavaEcommerce.Ecommerce.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // Find all items in an order
    List<OrderItem> findByOrderOrderId(Long orderId);

    // Find order items by product ID
    @Query("SELECT oi FROM OrderItem oi WHERE oi.product.productId = ?1")
    List<OrderItem> findByProductId(Long productId);

    // Find order items with discount
    @Query("SELECT oi FROM OrderItem oi WHERE oi.discount > 0 ORDER BY oi.discount DESC")
    List<OrderItem> findItemsWithDiscount();
}

