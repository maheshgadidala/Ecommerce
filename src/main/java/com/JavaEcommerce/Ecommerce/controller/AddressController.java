package com.JavaEcommerce.Ecommerce.controller;

import com.JavaEcommerce.Ecommerce.payload.AddressDto;
import com.JavaEcommerce.Ecommerce.request.CreateAddressRequest;
import com.JavaEcommerce.Ecommerce.request.UpdateAddressRequest;
import com.JavaEcommerce.Ecommerce.response.AddressResponse;
import com.JavaEcommerce.Ecommerce.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @PostMapping
    public ResponseEntity<AddressResponse> createAddress(@RequestBody CreateAddressRequest request) {
        AddressResponse response = addressService.createAddress(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AddressDto>> getUserAddresses() {
        List<AddressDto> addresses = addressService.getUserAddresses();
        return new ResponseEntity<>(addresses, HttpStatus.OK);
    }

    @GetMapping("/default")
    public ResponseEntity<AddressDto> getDefaultAddress() {
        AddressDto address = addressService.getDefaultAddress();
        return new ResponseEntity<>(address, HttpStatus.OK);
    }

    @GetMapping("/{addressId}")
    public ResponseEntity<AddressDto> getAddressById(@PathVariable Long addressId) {
        AddressDto address = addressService.getAddressById(addressId);
        return new ResponseEntity<>(address, HttpStatus.OK);
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<AddressResponse> updateAddress(
            @PathVariable Long addressId,
            @RequestBody UpdateAddressRequest request) {
        AddressResponse response = addressService.updateAddress(addressId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<AddressResponse> deleteAddress(@PathVariable Long addressId) {
        AddressResponse response = addressService.deleteAddress(addressId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{addressId}/set-default")
    public ResponseEntity<AddressResponse> setDefaultAddress(@PathVariable Long addressId) {
        AddressResponse response = addressService.setDefaultAddress(addressId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/type/{addressType}")
    public ResponseEntity<List<AddressDto>> getAddressesByType(@PathVariable String addressType) {
        List<AddressDto> addresses = addressService.getAddressesByType(addressType);
        return new ResponseEntity<>(addresses, HttpStatus.OK);
    }
}

