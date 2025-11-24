package com.JavaEcommerce.Ecommerce.payload;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDto {


    private Long cartItemId;
    private  CartDto cart;
    private ProductDto product;
    private Integer quantity;
    private Double totalPrice;
    private Double discountPrice;
    private Double finalProductPrice;

}
