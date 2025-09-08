package com.JavaEcommerce.Ecommerce.service;

import com.JavaEcommerce.Ecommerce.payload.ProductDto;
import com.JavaEcommerce.Ecommerce.payload.ProductResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface ProductService {
    ProductDto addprduct(Long categoryId, ProductDto productDto);

    ProductResponse getallProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortDir);

    ProductResponse searchByCategory(Long categoryId);

    ProductResponse searchProductKeyword(String keyword);

    ProductDto updadateProduct(ProductDto productDto, Long productId);

    ProductDto deleteProduct(Long prodcutId);

    ProductDto updateproductImage(Long productid, MultipartFile image) throws IOException;
}
