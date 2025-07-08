package com.JavaEcommerce.Ecommerce.controller;


import com.JavaEcommerce.Ecommerce.model.Product;
import com.JavaEcommerce.Ecommerce.payload.ProductDto;
import com.JavaEcommerce.Ecommerce.payload.ProductResponse;
import com.JavaEcommerce.Ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")

public class ProductController {
    @Autowired
    ProductService productService;

    @PostMapping("/admin/category/{categoryId}/product")
    public ResponseEntity<ProductDto> addProduct(@RequestBody ProductDto productDto,
                                                 @PathVariable Long categoryId) {

        ProductDto savedProductDto = productService.addprduct(categoryId, productDto);
        return new ResponseEntity<>(savedProductDto, HttpStatus.CREATED);
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
    public ResponseEntity<ProductDto> updateProduct(@RequestBody ProductDto productDto,
                                                    @PathVariable Long productId){
        ProductDto updatedProduct=productService.updadateProduct(productDto,productId);
        return new ResponseEntity<>(updatedProduct,HttpStatus.OK);
    }
    @DeleteMapping("/admin/products/{prodcutId}")
    public ResponseEntity<ProductDto> deleteProduct(@PathVariable Long prodcutId){
      ProductDto savedProducts=  productService.deleteProduct(prodcutId);
      return new ResponseEntity<>(savedProducts,HttpStatus.OK);
    }
    @PutMapping("product/{productid}/image")
    public ResponseEntity<ProductDto> uploadImage(@PathVariable Long productid,
                                                  @RequestParam ("Image")MultipartFile image){
       ProductDto updatedProduct= productService.updateproductImage(productid,image);
        return new ResponseEntity<>(updatedProduct,HttpStatus.OK);
    }
}
