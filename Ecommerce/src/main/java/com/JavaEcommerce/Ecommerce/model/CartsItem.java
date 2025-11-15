package com.JavaEcommerce.Ecommerce.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="CartsItems")

public class CartsItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartItemId;
    private Long cartId;
    private Long productId;
    private Integer quantity;
    private Double TotalPrice;
    private Double discountPrice;
    private Double finalProductPrice;


    @ManyToOne
    @JoinColumn(name="cartId", insertable = false, updatable = false)
    private Cart cart;

    @ManyToOne
    @JoinColumn(name="productId", insertable = false, updatable = false)
    private Product product;
}
