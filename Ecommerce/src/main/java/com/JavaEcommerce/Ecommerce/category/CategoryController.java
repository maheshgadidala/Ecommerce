package com.JavaEcommerce.Ecommerce.category;

import com.JavaEcommerce.Ecommerce.model.Category;
import com.JavaEcommerce.Ecommerce.payload.CategoryDto;
import com.JavaEcommerce.Ecommerce.payload.CategoryResponseDto;
import com.JavaEcommerce.Ecommerce.repo.CategoryRepository;
import com.JavaEcommerce.Ecommerce.service.CategoryService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
@RestController
@RequestMapping("/api")
public class CategoryController {

    @Autowired
   private CategoryService categoryService;
    @Autowired
   private ModelMapper modelMapper;


    @GetMapping("/echo")
    public ResponseEntity<String> echosMessage(@RequestParam(name="message",required = false) String message){
        return new ResponseEntity<>("Echoed message"+message,HttpStatus.OK);
    }
    @GetMapping("/categories")
    public ResponseEntity<CategoryResponseDto>  getCategories(@RequestParam(name="PageNumber")Integer pageNumber,
                                                              @RequestParam(name="PageSize")Integer pageSize){
        CategoryResponseDto categoryResponseDto= categoryService.getAllCategories(pageNumber,pageSize);
        return new ResponseEntity<>(categoryResponseDto,HttpStatus.OK);
    }

    @PostMapping("/categories")
    public ResponseEntity<CategoryDto> addCategory(@Valid @RequestBody CategoryDto categoryDto){
      CategoryDto savedCategoryDto= categoryService.createCategory(categoryDto);
        return new ResponseEntity<>(savedCategoryDto,HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDto> deleteCategory(@PathVariable Long categoryId){
            CategoryDto status= categoryService.deleteCategory(categoryId);
            return new ResponseEntity<>(status, HttpStatus.OK);
    }
    @PatchMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDto> updateCategory(@Valid @RequestBody CategoryDto categoryDto,
                                                      @PathVariable Long categoryId) {
        CategoryDto savedCategoryDto = categoryService.updateCategory(categoryDto, categoryId);
        return ResponseEntity.ok(savedCategoryDto);
    }
}
