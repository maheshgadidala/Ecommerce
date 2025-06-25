package com.JavaEcommerce.Ecommerce.service;


import com.JavaEcommerce.Ecommerce.payload.CategoryDto;
import com.JavaEcommerce.Ecommerce.payload.CategoryResponseDto;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public interface CategoryService {

    CategoryResponseDto getAllCategories(Integer pageNumber, Integer pageSize,String sortBy, String sortOrder);

    CategoryResponseDto getAllCategories(Integer pageNumber, Integer pageSize);

    CategoryDto createCategory(CategoryDto categoryDto);

    CategoryDto deleteCategory(Long categoryId);

    CategoryDto updateCategory(@Valid CategoryDto category, Long categoryId);
}
