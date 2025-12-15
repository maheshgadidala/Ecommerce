package com.JavaEcommerce.Ecommerce.repo;

import com.JavaEcommerce.Ecommerce.model.Payment;
import com.JavaEcommerce.Ecommerce.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Find payments by order ID
    List<Payment> findByOrderOrderId(Long orderId);

    // Find payments by user ID
    List<Payment> findByUserUserid(Long userId);

    // Find payments by user email
    @Query("SELECT p FROM Payment p WHERE p.user.userEmail = ?1")
    List<Payment> findByUserEmail(String userEmail);

    // Find payments by status
    List<Payment> findByPaymentStatus(PaymentStatus paymentStatus);

    // Find payments by transaction ID
    Optional<Payment> findByTransactionId(String transactionId);

    // Find payments by user and status
    @Query("SELECT p FROM Payment p WHERE p.user.userid = ?1 AND p.paymentStatus = ?2 ORDER BY p.paymentDate DESC")
    List<Payment> findByUserAndStatus(Long userId, PaymentStatus paymentStatus);

    // Find payments by date range
    @Query("SELECT p FROM Payment p WHERE p.paymentDate BETWEEN ?1 AND ?2 ORDER BY p.paymentDate DESC")
    List<Payment> findPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    // Find recent payments for a user
    @Query("SELECT p FROM Payment p WHERE p.user.userid = ?1 ORDER BY p.paymentDate DESC")
    List<Payment> findRecentPaymentsByUser(Long userId);

    // Find failed payments
    List<Payment> findByPaymentStatusAndUserUserid(PaymentStatus paymentStatus, Long userId);
}

