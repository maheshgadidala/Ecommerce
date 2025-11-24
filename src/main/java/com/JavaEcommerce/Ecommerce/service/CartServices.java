package com.JavaEcommerce.Ecommerce.service;


import com.JavaEcommerce.Ecommerce.payload.CartDto;


public interface CartServices {

    CartDto addProductsToCart(Long productId, Integer quantity);
}
