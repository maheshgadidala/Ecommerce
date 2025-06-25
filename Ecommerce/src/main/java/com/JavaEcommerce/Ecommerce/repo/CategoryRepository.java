package com.JavaEcommerce.Ecommerce.repo;

import com.JavaEcommerce.Ecommerce.model.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {
    Optional<Category> findByCategoryName(@NotBlank(message = "categoryname must not be empty") @Size(min = 4) String categoryName);
}
