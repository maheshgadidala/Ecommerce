package com.JavaEcommerce.Ecommerce.repo;

import com.JavaEcommerce.Ecommerce.model.Order;
import com.JavaEcommerce.Ecommerce.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Find orders by user ID
    List<Order> findByUserUserid(Long userId);

    // Find orders by user email
    @Query("SELECT o FROM Order o WHERE o.user.userEmail = ?1")
    List<Order> findByUserEmail(String userEmail);

    // Find orders by status
    List<Order> findByOrderStatus(OrderStatus orderStatus);

    // Find orders by user ID and status
    @Query("SELECT o FROM Order o WHERE o.user.userid = ?1 AND o.orderStatus = ?2")
    List<Order> findByUserAndStatus(Long userId, OrderStatus orderStatus);

    // Find orders by date range
    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN ?1 AND ?2")
    List<Order> findOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    // Find recent orders for a user
    @Query("SELECT o FROM Order o WHERE o.user.userid = ?1 ORDER BY o.orderDate DESC")
    List<Order> findRecentOrdersByUser(Long userId);

    // Find orders with pending payment
    @Query("SELECT o FROM Order o WHERE o.user.userid = ?1 AND o.orderStatus != 'DELIVERED' ORDER BY o.orderDate DESC")
    List<Order> findPendingOrders(Long userId);
}

