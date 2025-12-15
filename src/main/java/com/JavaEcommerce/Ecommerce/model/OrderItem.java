package com.JavaEcommerce.Ecommerce.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private Integer quantity;

    private Double pricePerUnit;

    private Double discount = 0.0;

    private Double itemTotal;

    @PrePersist
    protected void calculateItemTotal() {
        if (pricePerUnit != null && quantity != null) {
            itemTotal = (pricePerUnit * quantity) - (discount != null ? discount : 0.0);
        }
    }
}

