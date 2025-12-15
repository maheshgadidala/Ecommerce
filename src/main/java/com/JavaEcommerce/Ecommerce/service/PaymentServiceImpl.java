package com.JavaEcommerce.Ecommerce.service;

import com.JavaEcommerce.Ecommerce.exception.ApiException;
import com.JavaEcommerce.Ecommerce.exception.ResourceNotFoundException;
import com.JavaEcommerce.Ecommerce.model.*;
import com.JavaEcommerce.Ecommerce.payload.PaymentDto;
import com.JavaEcommerce.Ecommerce.repo.OrderRepository;
import com.JavaEcommerce.Ecommerce.repo.PaymentRepository;
import com.JavaEcommerce.Ecommerce.repo.UserRepository;
import com.JavaEcommerce.Ecommerce.request.ProcessPaymentRequest;
import com.JavaEcommerce.Ecommerce.response.PaymentResponse;
import com.JavaEcommerce.Ecommerce.util.AuthUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthUtils authUtils;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PaymentResponse processPayment(ProcessPaymentRequest request) {
        try {
            logger.info("Processing payment for order ID: {}", request.getOrderId());

            // Get logged-in user
            User user = userRepository.findByUserName(authUtils.loggedInUserEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "email", authUtils.loggedInUserEmail()));

            logger.debug("User found: {}", user.getUserEmail());

            // Get order
            Order order = orderRepository.findById(request.getOrderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Order", "orderId", request.getOrderId()));

            logger.debug("Order found: {}", order.getOrderId());

            // Validate payment amount
            if (!request.getAmount().equals(order.getFinalAmount())) {
                logger.warn("Payment amount mismatch. Expected: {}, Received: {}",
                    order.getFinalAmount(), request.getAmount());
                throw new ApiException("Payment amount does not match order total");
            }

            // Create payment
            Payment payment = new Payment();
            payment.setOrder(order);
            payment.setUser(user);
            payment.setAmount(request.getAmount());
            payment.setPaymentStatus(PaymentStatus.PROCESSING);

            try {
                payment.setPaymentMethod(PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase()));
            } catch (IllegalArgumentException e) {
                payment.setPaymentMethod(PaymentMethod.CREDIT_CARD);
            }

            payment.setPaymentGateway(request.getPaymentGateway());

            // Generate transaction ID (in real scenario, this would come from payment gateway)
            payment.setTransactionId(generateTransactionId());
            payment.setPaymentReference(UUID.randomUUID().toString());

            logger.debug("Payment object created with Transaction ID: {}", payment.getTransactionId());

            // Process payment (mock implementation)
            processPaymentWithGateway(payment);

            // Save payment
            Payment savedPayment = paymentRepository.save(payment);
            logger.info("Payment processed successfully with ID: {}", savedPayment.getPaymentId());

            // Update order status if payment successful
            if (savedPayment.getPaymentStatus() == PaymentStatus.COMPLETED) {
                order.setOrderStatus(OrderStatus.CONFIRMED);
                orderRepository.save(order);
                logger.debug("Order status updated to CONFIRMED");
            }

            // Convert to DTO
            PaymentDto paymentDto = convertToPaymentDto(savedPayment);

            return PaymentResponse.builder()
                    .success(true)
                    .message("Payment processed successfully")
                    .timestamp(LocalDateTime.now())
                    .payment(paymentDto)
                    .transactionId(savedPayment.getTransactionId())
                    .status("201 CREATED")
                    .build();

        } catch (ResourceNotFoundException | ApiException e) {
            logger.error("Error processing payment: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error while processing payment", e);
            throw new ApiException("Error processing payment: " + e.getMessage());
        }
    }

    @Override
    public PaymentDto getPaymentById(Long paymentId) {
        try {
            logger.info("Fetching payment by ID: {}", paymentId);

            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Payment", "paymentId", paymentId));

            logger.debug("Payment found");
            return convertToPaymentDto(payment);

        } catch (Exception e) {
            logger.error("Error fetching payment: {}", e.getMessage());
            throw new ApiException("Error fetching payment: " + e.getMessage());
        }
    }

    @Override
    public List<PaymentDto> getUserPayments() {
        try {
            logger.info("Fetching payments for user");

            User user = userRepository.findByUserName(authUtils.loggedInUserEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "email", authUtils.loggedInUserEmail()));

            List<Payment> payments = paymentRepository.findByUserUserid(user.getUserid());
            logger.debug("Found {} payments", payments.size());

            return payments.stream()
                    .map(this::convertToPaymentDto)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            logger.error("Error fetching user payments: {}", e.getMessage());
            throw new ApiException("Error fetching payments: " + e.getMessage());
        }
    }

    @Override
    public List<PaymentDto> getPaymentsByStatus(String status) {
        try {
            logger.info("Fetching payments by status: {}", status);

            PaymentStatus paymentStatus = PaymentStatus.valueOf(status.toUpperCase());
            List<Payment> payments = paymentRepository.findByPaymentStatus(paymentStatus);
            logger.debug("Found {} payments with status {}", payments.size(), status);

            return payments.stream()
                    .map(this::convertToPaymentDto)
                    .collect(Collectors.toList());

        } catch (IllegalArgumentException e) {
            logger.error("Invalid payment status: {}", status);
            throw new ApiException("Invalid payment status: " + status);
        } catch (Exception e) {
            logger.error("Error fetching payments by status: {}", e.getMessage());
            throw new ApiException("Error fetching payments: " + e.getMessage());
        }
    }

    @Override
    public PaymentDto getPaymentByTransactionId(String transactionId) {
        try {
            logger.info("Fetching payment by transaction ID: {}", transactionId);

            Payment payment = paymentRepository.findByTransactionId(transactionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Payment", "transactionId", transactionId));

            logger.debug("Payment found");
            return convertToPaymentDto(payment);

        } catch (Exception e) {
            logger.error("Error fetching payment by transaction ID: {}", e.getMessage());
            throw new ApiException("Error fetching payment: " + e.getMessage());
        }
    }

    @Override
    public List<PaymentDto> getPaymentHistory(int pageNumber, int pageSize) {
        try {
            logger.info("Fetching payment history - Page: {}, Size: {}", pageNumber, pageSize);

            User user = userRepository.findByUserName(authUtils.loggedInUserEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "email", authUtils.loggedInUserEmail()));

            List<Payment> payments = paymentRepository.findRecentPaymentsByUser(user.getUserid());
            logger.debug("Found {} payments in history", payments.size());

            return payments.stream()
                    .map(this::convertToPaymentDto)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            logger.error("Error fetching payment history: {}", e.getMessage());
            throw new ApiException("Error fetching payment history: " + e.getMessage());
        }
    }

    @Override
    public PaymentResponse refundPayment(Long paymentId) {
        try {
            logger.info("Refunding payment with ID: {}", paymentId);

            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Payment", "paymentId", paymentId));

            // Check if payment can be refunded
            if (payment.getPaymentStatus() != PaymentStatus.COMPLETED) {
                throw new ApiException("Only completed payments can be refunded");
            }

            payment.setPaymentStatus(PaymentStatus.REFUNDED);
            Payment refundedPayment = paymentRepository.save(payment);
            logger.info("Payment refunded successfully");

            PaymentDto paymentDto = convertToPaymentDto(refundedPayment);

            return PaymentResponse.builder()
                    .success(true)
                    .message("Payment refunded successfully")
                    .timestamp(LocalDateTime.now())
                    .payment(paymentDto)
                    .status("200 OK")
                    .build();

        } catch (Exception e) {
            logger.error("Error refunding payment: {}", e.getMessage());
            throw new ApiException("Error refunding payment: " + e.getMessage());
        }
    }

    @Override
    public PaymentDto getPaymentReceipt(Long orderId) {
        try {
            logger.info("Fetching payment receipt for order ID: {}", orderId);

            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new ResourceNotFoundException("Order", "orderId", orderId));

            // Get the latest payment for this order
            List<Payment> payments = paymentRepository.findByOrderOrderId(orderId);
            if (payments.isEmpty()) {
                throw new ResourceNotFoundException("Payment", "orderId", orderId);
            }

            Payment latestPayment = payments.stream()
                    .filter(p -> p.getPaymentStatus() == PaymentStatus.COMPLETED)
                    .findFirst()
                    .orElseThrow(() -> new ApiException("No completed payment found for this order"));

            logger.debug("Payment receipt found");
            return convertToPaymentDto(latestPayment);

        } catch (Exception e) {
            logger.error("Error fetching payment receipt: {}", e.getMessage());
            throw new ApiException("Error fetching payment receipt: " + e.getMessage());
        }
    }

    @Override
    public String checkPaymentStatus(Long paymentId) {
        try {
            logger.info("Checking payment status for payment ID: {}", paymentId);

            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Payment", "paymentId", paymentId));

            logger.debug("Payment status: {}", payment.getPaymentStatus());
            return payment.getPaymentStatus().getDisplayName();

        } catch (Exception e) {
            logger.error("Error checking payment status: {}", e.getMessage());
            throw new ApiException("Error checking payment status: " + e.getMessage());
        }
    }

    private PaymentDto convertToPaymentDto(Payment payment) {
        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setPaymentId(payment.getPaymentId());
        paymentDto.setOrderId(payment.getOrder().getOrderId());
        paymentDto.setUserId(payment.getUser().getUserid());
        paymentDto.setAmount(payment.getAmount());
        paymentDto.setPaymentStatus(payment.getPaymentStatus().toString());
        paymentDto.setPaymentMethod(payment.getPaymentMethod().toString());
        paymentDto.setTransactionId(payment.getTransactionId());
        paymentDto.setPaymentGateway(payment.getPaymentGateway());
        paymentDto.setPaymentReference(payment.getPaymentReference());
        paymentDto.setPaymentDate(payment.getPaymentDate());
        paymentDto.setNotes(payment.getNotes());
        return paymentDto;
    }

    private void processPaymentWithGateway(Payment payment) {
        // Mock payment processing implementation
        logger.debug("Processing payment with gateway: {}", payment.getPaymentGateway());

        // Simulate payment processing delay
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Mock success (in real implementation, call actual payment gateway API)
        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        payment.setPaymentDate(LocalDateTime.now());
        logger.debug("Payment processing completed (mock)");
    }

    private String generateTransactionId() {
        // Generate unique transaction ID
        return "TXN-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 10000);
    }
}

