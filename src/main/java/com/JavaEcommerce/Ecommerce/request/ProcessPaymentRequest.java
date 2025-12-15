package com.JavaEcommerce.Ecommerce.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessPaymentRequest {

    private Long orderId;
    private Double amount;
    private String paymentMethod;
    private String paymentGateway = "MOCK";
}

