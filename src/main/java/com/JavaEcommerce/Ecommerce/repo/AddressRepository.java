package com.JavaEcommerce.Ecommerce.repo;

import com.JavaEcommerce.Ecommerce.model.Address;
import com.JavaEcommerce.Ecommerce.model.AddressType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    // Find addresses by user ID
    @Query("SELECT a FROM Address a JOIN a.users u WHERE u.userid = ?1")
    List<Address> findByUserId(Long userId);

    // Find default address for a user
    @Query("SELECT a FROM Address a JOIN a.users u WHERE u.userid = ?1 AND a.isDefault = true")
    Optional<Address> findDefaultAddressByUserId(Long userId);

    // Find addresses by type for a user
    @Query("SELECT a FROM Address a JOIN a.users u WHERE u.userid = ?1 AND a.addressType = ?2")
    List<Address> findByUserIdAndType(Long userId, AddressType addressType);

    // Find address by city
    List<Address> findByCity(String city);

    // Find address by zip code
    List<Address> findByZipCode(String zipCode);

    // Find addresses by country
    List<Address> findByCountry(String country);
}

