package com.JavaEcommerce.Ecommerce.controller;

import com.JavaEcommerce.Ecommerce.config.AppConstant;
import com.JavaEcommerce.Ecommerce.payload.CategoryDto;
import com.JavaEcommerce.Ecommerce.payload.CategoryResponseDto;
import com.JavaEcommerce.Ecommerce.service.CategoryService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CategoryController {

    @Autowired
   private CategoryService categoryService;
    @Autowired
   private ModelMapper modelMapper;

    @GetMapping("/public/getcategories")
    public ResponseEntity<CategoryResponseDto> getCategories(
            @RequestParam(name = "PageNumber", defaultValue = AppConstant.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "PageSize", defaultValue = AppConstant.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstant.SORT_CATEGORIES, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstant.SORT_BY_DIR, required = false) String sortOrder) {

        CategoryResponseDto categoryResponseDto = categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortOrder);
        return ResponseEntity.ok(categoryResponseDto);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/addcategories")
    public ResponseEntity<CategoryDto> addCategory(@Valid @RequestBody CategoryDto categoryDto){
      CategoryDto savedCategoryDto= categoryService.createCategory(categoryDto);
        return new ResponseEntity<>(savedCategoryDto,HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDto> deleteCategory(@PathVariable Long categoryId){
            CategoryDto status= categoryService.deleteCategory(categoryId);
            return new ResponseEntity<>(status, HttpStatus.OK);
    }
    @PatchMapping("/admin/categories/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDto> updateCategory(@Valid @RequestBody CategoryDto categoryDto,
                                                      @PathVariable Long categoryId) {
        CategoryDto savedCategoryDto = categoryService.updateCategory(categoryDto, categoryId);
        return ResponseEntity.ok(savedCategoryDto);
    }
}
