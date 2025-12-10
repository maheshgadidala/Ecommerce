package com.JavaEcommerce.Ecommerce.response;

import com.JavaEcommerce.Ecommerce.payload.CartDto;
import com.JavaEcommerce.Ecommerce.payload.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartResponse {

    private boolean success;
    private String message;
    private LocalDateTime timestamp;
    private CartDto cart;
    private ProductDto addedProduct;
    private Integer quantityAdded;
    private Double priceAdded;
    private String status;
}

