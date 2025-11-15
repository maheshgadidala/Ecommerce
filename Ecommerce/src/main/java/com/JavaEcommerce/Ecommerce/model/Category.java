package com.JavaEcommerce.Ecommerce.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.boot.autoconfigure.web.WebProperties;

import java.util.List;

@Entity
@Table(name = "category")
@Data
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name="category_id")
    private Long categoryId;
    @NotBlank(message = "categoryname must not be empty")
    @Size(min = 4)
    @Column(name = "category_name",unique = true)
    private String categoryName;

    @OneToMany(mappedBy = "category",cascade = CascadeType.ALL)
    List<Product>product;
}
