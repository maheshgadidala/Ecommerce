package com.JavaEcommerce.Ecommerce.response;

import com.JavaEcommerce.Ecommerce.payload.PaymentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private boolean success;
    private String message;
    private LocalDateTime timestamp;
    private PaymentDto payment;
    private String status;
    private String transactionId;
}

