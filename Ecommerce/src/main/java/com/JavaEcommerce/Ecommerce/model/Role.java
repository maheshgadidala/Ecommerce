package com.JavaEcommerce.Ecommerce.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "role_id")
    private Integer roleId;
    @Column(length = 20 ,name = "role_name", nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    @ToString.Exclude
    private AppRole rollName;

    public Role(AppRole rollName) {
        this.rollName = rollName;
    }
}
