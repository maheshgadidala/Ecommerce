package com.JavaEcommerce.Ecommerce.service;

import com.JavaEcommerce.Ecommerce.payload.PaymentDto;
import com.JavaEcommerce.Ecommerce.request.ProcessPaymentRequest;
import com.JavaEcommerce.Ecommerce.response.PaymentResponse;

import java.util.List;

public interface PaymentService {

    // Process payment for an order
    PaymentResponse processPayment(ProcessPaymentRequest request);

    // Get payment details by ID
    PaymentDto getPaymentById(Long paymentId);

    // Get payments for logged-in user
    List<PaymentDto> getUserPayments();

    // Get payments by status
    List<PaymentDto> getPaymentsByStatus(String status);

    // Get payment by transaction ID
    PaymentDto getPaymentByTransactionId(String transactionId);

    // Get payment history with pagination
    List<PaymentDto> getPaymentHistory(int pageNumber, int pageSize);

    // Refund payment
    PaymentResponse refundPayment(Long paymentId);

    // Get payment receipt
    PaymentDto getPaymentReceipt(Long orderId);

    // Check payment status
    String checkPaymentStatus(Long paymentId);
}

