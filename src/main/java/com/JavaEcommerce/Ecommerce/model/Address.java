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
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int addressId;

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

    @ToString.Exclude
    @ManyToMany(mappedBy = "addresses")
    private List<User> users=new ArrayList<>();



}
