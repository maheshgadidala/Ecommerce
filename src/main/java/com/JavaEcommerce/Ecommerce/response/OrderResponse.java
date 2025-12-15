package com.JavaEcommerce.Ecommerce.response;

import com.JavaEcommerce.Ecommerce.payload.OrderDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private boolean success;
    private String message;
    private LocalDateTime timestamp;
    private OrderDto order;
    private String status;
}

