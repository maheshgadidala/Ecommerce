package com.JavaEcommerce.Ecommerce.service;


import com.JavaEcommerce.Ecommerce.exception.ApiException;
import com.JavaEcommerce.Ecommerce.exception.ResourceNotFoundException;
import com.JavaEcommerce.Ecommerce.payload.CategoryDto;
import com.JavaEcommerce.Ecommerce.payload.CategoryResponseDto;
import com.JavaEcommerce.Ecommerce.repo.CategoryRepository;
import com.JavaEcommerce.Ecommerce.model.Category;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    ModelMapper modelMapper;


    @Override
    public CategoryResponseDto getAllCategories(Integer pageNumber, Integer pageSize,
                                                String sortBy, String sortOrder) {
        Sort sortByOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);
        List<Category> categories = categoryPage.getContent();

        if (categories.isEmpty()) {
            throw new ApiException("No categories found");
        }

        List<CategoryDto> categoryDtos = categories.stream()
                .map(category -> modelMapper.map(category, CategoryDto.class))
                .toList();

        CategoryResponseDto categoryResponseDto = new CategoryResponseDto();
        categoryResponseDto.setContent(categoryDtos);
        categoryResponseDto.setPageNumber(categoryPage.getNumber());
        categoryResponseDto.setPageSize(categoryPage.getSize());
        categoryResponseDto.setTotalPages(categoryPage.getTotalPages());
        categoryResponseDto.setTotalElements(categoryPage.getTotalElements());
        categoryResponseDto.setLastPage(categoryPage.isLast());

        return categoryResponseDto;
    }

    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category category = modelMapper.map(categoryDto, Category.class);
        category.setCategoryId(null);
        Optional<Category> existingCategory = categoryRepository.findByCategoryName(category.getCategoryName());

        if(existingCategory.isPresent()) {
            throw new ApiException("Category already exists with the name: " + category.getCategoryName());
        }

        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDto.class);
    }

    @Override
    public CategoryDto deleteCategory(Long categoryId) {
        Category categories=categoryRepository.findById(categoryId)
                .orElseThrow(()->new ResourceNotFoundException("Category","CategoryId",categoryId));

        categoryRepository.delete(categories);
        return modelMapper.map(categories, CategoryDto.class);
    }


    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto, Long categoryId) {
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", categoryId));

        Category category = modelMapper.map(categoryDto, Category.class);
        category.setCategoryId(categoryId);
        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDto.class);
    }
}
