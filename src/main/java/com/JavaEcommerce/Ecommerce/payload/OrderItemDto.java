package com.JavaEcommerce.Ecommerce.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {

    private Long orderItemId;
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double pricePerUnit;
    private Double discount;
    private Double itemTotal;
}

