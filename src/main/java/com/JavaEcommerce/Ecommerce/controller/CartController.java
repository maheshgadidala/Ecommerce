package com.JavaEcommerce.Ecommerce.controller;

import com.JavaEcommerce.Ecommerce.payload.CartDto;
import com.JavaEcommerce.Ecommerce.response.CartResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/")
public class CartController {

    @Autowired
    private com.JavaEcommerce.Ecommerce.service.CartServices cartService;

    @PostMapping("/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartResponse> addProductsToCart(@PathVariable Long productId,
                                             @PathVariable Integer quantity) {
        CartResponse cartResponse = cartService.addProductsToCart(productId, quantity);
        return new ResponseEntity<>(cartResponse, HttpStatus.CREATED);
    }

    @GetMapping("/carts")
    public ResponseEntity<List<CartDto>> getCartItems() {
        List<CartDto> cartDtos=cartService.getAllCarts();
        return new ResponseEntity<List<CartDto>>(cartDtos, HttpStatus.FOUND);
    }

    @PutMapping("/cart/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartResponse> updateProductQuantityInCart(@PathVariable Long productId,
                                                                    @PathVariable Integer quantity) {
        CartResponse cartResponse = cartService.updateProductQuantityInCart(productId, quantity);
        return new ResponseEntity<>(cartResponse, HttpStatus.OK);
    }

    @DeleteMapping("/cart/products/{productId}")
    public ResponseEntity<CartResponse> deleteProductFromCart(@PathVariable Long productId) {
        CartResponse cartResponse = cartService.deleteProductFromCart(productId);
        return new ResponseEntity<>(cartResponse, HttpStatus.OK);
    }

    @GetMapping("/cart")
    public ResponseEntity<CartResponse> getCartDetails() {
        CartResponse cartResponse = cartService.getCartDetails();
        return new ResponseEntity<>(cartResponse, HttpStatus.OK);
    }

    @DeleteMapping("/cart")
    public ResponseEntity<CartResponse> clearCart() {
        CartResponse cartResponse = cartService.clearCart();
        return new ResponseEntity<>(cartResponse, HttpStatus.OK);
    }
}
