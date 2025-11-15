package com.JavaEcommerce.Ecommerce.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "app_users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "user_email"),
                @UniqueConstraint(columnNames = "user_name")})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userid;

    @Column(name = "user_name", nullable = false, length = 20)
    private String userName;

    @Size(max = 50)
    @Column(name = "user_email", nullable = false, unique = true)
    @Email
    private String userEmail;

    @Column(name = "user_password", nullable = false)
    @Size(min = 8, max = 100, message = "Password must be at least 8 characters long")
    private String password;

    // db relationships
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(name = "app_user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    // OneToMany: mappedBy must match the field name on the target entity (Product.user)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Product> products = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "user_addresses",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "address_id"))
    private Set<Address> addresses = new HashSet<>();

}
