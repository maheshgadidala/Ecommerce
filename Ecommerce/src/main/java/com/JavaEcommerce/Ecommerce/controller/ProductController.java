package com.JavaEcommerce.Ecommerce.controller;


import com.JavaEcommerce.Ecommerce.model.Product;
import com.JavaEcommerce.Ecommerce.payload.ProductDto;
import com.JavaEcommerce.Ecommerce.payload.ProductResponse;
import com.JavaEcommerce.Ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")

public class ProductController {
    @Autowired
    ProductService productService;

    @PostMapping("/admin/category/{categoryId}/product")
    public ResponseEntity<ProductDto> addProduct(@RequestBody Product product,
                                                 @PathVariable Long categoryId) {

        ProductDto productDto = productService.addprduct(categoryId, product);
        return new ResponseEntity<>(productDto, HttpStatus.CREATED);
    }

    @GetMapping("/public/getProducts")
    public ResponseEntity<ProductResponse> getAllProducts() {
        ProductResponse productResponse = productService.getallProducts();
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @GetMapping("/public/category/{categoryId}/product")
    public ResponseEntity<ProductResponse> getProductsByCategory(@PathVariable Long categoryId) {
        ProductResponse productResponse = productService.searchByCategory(categoryId);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductsByCategory(@PathVariable String keyword) {
        ProductResponse productResponse = productService.searchProductKeyword(keyword);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }
    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDto> updateProduct(@RequestBody Product product,
                                                    @PathVariable Long productId){
        ProductDto updatedProduct=productService.updadateProduct(product,productId);
        return new ResponseEntity<>(updatedProduct,HttpStatus.OK);
    }
}
