package com.JavaEcommerce.Ecommerce.service;


import com.JavaEcommerce.Ecommerce.payload.CartDto;
import com.JavaEcommerce.Ecommerce.response.CartResponse;

import java.util.List;


public interface CartServices {

    CartResponse addProductsToCart(Long productId, Integer quantity);

    List<CartDto> getAllCarts();

    CartResponse updateProductQuantityInCart(Long productId, Integer quantity);

    CartResponse deleteProductFromCart(Long productId);

    CartResponse getCartDetails();

    CartResponse clearCart();
}
