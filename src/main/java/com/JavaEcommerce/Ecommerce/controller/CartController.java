package com.JavaEcommerce.Ecommerce.controller;

import com.JavaEcommerce.Ecommerce.payload.CartDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    @Autowired
    private com.JavaEcommerce.Ecommerce.service.CartServices cartService;

    @PostMapping("/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDto> addProductsToCart(@PathVariable Long productId,
                                             @PathVariable Integer quantity) {
        CartDto cartDto = cartService.addProductsToCart(productId, quantity);
        return new ResponseEntity<>(cartDto, HttpStatus.CREATED);
    }
}
