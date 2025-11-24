package com.JavaEcommerce.Ecommerce.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDto {


    private long cartId;
    private double totalPrice;
    private List<ProductDto> products;
}
