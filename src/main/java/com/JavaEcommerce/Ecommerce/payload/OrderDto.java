package com.JavaEcommerce.Ecommerce.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    private Long orderId;
    private Long userId;
    private String userName;
    private String userEmail;
    private Long addressId;
    private String shippingAddressDetails;
    private List<OrderItemDto> orderItems;
    private String orderStatus;
    private Double totalAmount;
    private Double discountAmount;
    private Double finalAmount;
    private LocalDateTime orderDate;
    private LocalDateTime estimatedDeliveryDate;
    private LocalDateTime deliveryDate;
    private String orderNotes;
}

