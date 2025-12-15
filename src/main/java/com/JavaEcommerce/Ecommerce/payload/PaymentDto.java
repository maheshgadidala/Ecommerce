package com.JavaEcommerce.Ecommerce.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {

    private Long paymentId;
    private Long orderId;
    private Long userId;
    private String transactionId;
    private Double amount;
    private String paymentStatus;
    private String paymentMethod;
    private String paymentGateway;
    private String paymentReference;
    private LocalDateTime paymentDate;
    private String notes;
}

