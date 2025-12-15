package com.JavaEcommerce.Ecommerce.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @NotBlank
    @Size(max = 100, message = "Street address must not exceed 100 characters")
    private String street;

    @NotBlank
    @Size(max = 50, message = "City must not exceed 50 characters")
    private String city;

    @NotBlank
    @Size(max = 50, message = "State must not exceed 50 characters")
    private String state;

    @NotBlank
    @Size(max = 50, message = "Country must not exceed 50 characters")
    private String country;

    @NotBlank
    @Size(max = 6, message = "Zip code must not exceed 6 characters")
    private String zipCode;

    @NotBlank
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phoneNumber;

    @Size(max = 50)
    private String recipientName;

    @Enumerated(EnumType.STRING)
    private AddressType addressType = AddressType.SHIPPING;

    private boolean isDefault = false;

    @ToString.Exclude
    @ManyToMany(mappedBy = "addresses", fetch = FetchType.LAZY)
    private List<User> users = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "shippingAddress", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();
}

