package com.JavaEcommerce.Ecommerce.service;

import com.JavaEcommerce.Ecommerce.model.Product;
import com.JavaEcommerce.Ecommerce.payload.ProductDto;
import com.JavaEcommerce.Ecommerce.payload.ProductResponse;
import org.springframework.stereotype.Service;

@Service
public interface ProductService {
    ProductDto addprduct(Long categoryId, Product product);

    ProductResponse getallProducts();

    ProductResponse searchByCategory(Long categoryId);

    ProductResponse searchProductKeyword(String keyword);

    ProductDto updadateProduct(Product product, Long productId);
}
