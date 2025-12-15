package com.JavaEcommerce.Ecommerce.service;

import com.JavaEcommerce.Ecommerce.payload.AddressDto;
import com.JavaEcommerce.Ecommerce.request.CreateAddressRequest;
import com.JavaEcommerce.Ecommerce.request.UpdateAddressRequest;
import com.JavaEcommerce.Ecommerce.response.AddressResponse;

import java.util.List;

public interface AddressService {

    // Create a new address for logged-in user
    AddressResponse createAddress(CreateAddressRequest request);

    // Get all addresses for logged-in user
    List<AddressDto> getUserAddresses();

    // Get default address for logged-in user
    AddressDto getDefaultAddress();

    // Get specific address by ID
    AddressDto getAddressById(Long addressId);

    // Update an address
    AddressResponse updateAddress(Long addressId, UpdateAddressRequest request);

    // Delete an address
    AddressResponse deleteAddress(Long addressId);

    // Set address as default
    AddressResponse setDefaultAddress(Long addressId);

    // Get addresses by type (SHIPPING/BILLING)
    List<AddressDto> getAddressesByType(String addressType);
}

