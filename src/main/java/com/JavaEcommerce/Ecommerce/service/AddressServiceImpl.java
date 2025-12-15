package com.JavaEcommerce.Ecommerce.service;

import com.JavaEcommerce.Ecommerce.exception.ApiException;
import com.JavaEcommerce.Ecommerce.exception.ResourceNotFoundException;
import com.JavaEcommerce.Ecommerce.model.Address;
import com.JavaEcommerce.Ecommerce.model.AddressType;
import com.JavaEcommerce.Ecommerce.model.User;
import com.JavaEcommerce.Ecommerce.payload.AddressDto;
import com.JavaEcommerce.Ecommerce.repo.AddressRepository;
import com.JavaEcommerce.Ecommerce.repo.UserRepository;
import com.JavaEcommerce.Ecommerce.request.CreateAddressRequest;
import com.JavaEcommerce.Ecommerce.request.UpdateAddressRequest;
import com.JavaEcommerce.Ecommerce.response.AddressResponse;
import com.JavaEcommerce.Ecommerce.util.AuthUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements AddressService {

    private static final Logger logger = LoggerFactory.getLogger(AddressServiceImpl.class);

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthUtils authUtils;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public AddressResponse createAddress(CreateAddressRequest request) {
        try {
            logger.info("Creating new address for user");

            // Get logged-in user
            User user = userRepository.findByUserName(authUtils.loggedInUserEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "email", authUtils.loggedInUserEmail()));

            logger.debug("User found: {}", user.getUserEmail());

            // Create new address
            Address address = new Address();
            address.setStreet(request.getStreet());
            address.setCity(request.getCity());
            address.setState(request.getState());
            address.setCountry(request.getCountry());
            address.setZipCode(request.getZipCode());
            address.setPhoneNumber(request.getPhoneNumber());
            address.setRecipientName(request.getRecipientName());

            // Set address type
            try {
                address.setAddressType(AddressType.valueOf(request.getAddressType().toUpperCase()));
            } catch (IllegalArgumentException e) {
                address.setAddressType(AddressType.SHIPPING);
            }

            address.setDefault(request.isDefault());

            // If this is set as default, remove default from other addresses
            if (request.isDefault()) {
                List<Address> userAddresses = addressRepository.findByUserId(user.getUserid());
                userAddresses.forEach(addr -> addr.setDefault(false));
                addressRepository.saveAll(userAddresses);
                logger.debug("Removed default flag from other addresses");
            }

            // Save address
            Address savedAddress = addressRepository.save(address);
            logger.debug("Address saved with ID: {}", savedAddress.getAddressId());

            // Associate address with user
            user.getAddresses().add(savedAddress);
            userRepository.save(user);
            logger.info("Address associated with user");

            // Convert to DTO
            AddressDto addressDto = modelMapper.map(savedAddress, AddressDto.class);

            return AddressResponse.builder()
                    .success(true)
                    .message("Address created successfully")
                    .timestamp(LocalDateTime.now())
                    .address(addressDto)
                    .status("201 CREATED")
                    .build();

        } catch (ResourceNotFoundException e) {
            logger.error("Error creating address: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error while creating address", e);
            throw new ApiException("Error creating address: " + e.getMessage());
        }
    }

    @Override
    public List<AddressDto> getUserAddresses() {
        try {
            logger.info("Fetching addresses for user");

            User user = userRepository.findByUserName(authUtils.loggedInUserEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "email", authUtils.loggedInUserEmail()));

            List<Address> addresses = addressRepository.findByUserId(user.getUserid());
            logger.debug("Found {} addresses for user", addresses.size());

            return addresses.stream()
                    .map(address -> modelMapper.map(address, AddressDto.class))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            logger.error("Error fetching user addresses: {}", e.getMessage());
            throw new ApiException("Error fetching addresses: " + e.getMessage());
        }
    }

    @Override
    public AddressDto getDefaultAddress() {
        try {
            logger.info("Fetching default address for user");

            User user = userRepository.findByUserName(authUtils.loggedInUserEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "email", authUtils.loggedInUserEmail()));

            Address address = addressRepository.findDefaultAddressByUserId(user.getUserid())
                    .orElseThrow(() -> new ResourceNotFoundException("Default Address", "user", user.getUserid()));

            logger.debug("Default address found: {}", address.getAddressId());
            return modelMapper.map(address, AddressDto.class);

        } catch (Exception e) {
            logger.error("Error fetching default address: {}", e.getMessage());
            throw new ApiException("Error fetching default address: " + e.getMessage());
        }
    }

    @Override
    public AddressDto getAddressById(Long addressId) {
        try {
            logger.info("Fetching address by ID: {}", addressId);

            Address address = addressRepository.findById(addressId)
                    .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

            logger.debug("Address found");
            return modelMapper.map(address, AddressDto.class);

        } catch (Exception e) {
            logger.error("Error fetching address: {}", e.getMessage());
            throw new ApiException("Error fetching address: " + e.getMessage());
        }
    }

    @Override
    public AddressResponse updateAddress(Long addressId, UpdateAddressRequest request) {
        try {
            logger.info("Updating address with ID: {}", addressId);

            Address address = addressRepository.findById(addressId)
                    .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

            // Update fields
            address.setStreet(request.getStreet());
            address.setCity(request.getCity());
            address.setState(request.getState());
            address.setCountry(request.getCountry());
            address.setZipCode(request.getZipCode());
            address.setPhoneNumber(request.getPhoneNumber());
            address.setRecipientName(request.getRecipientName());

            try {
                address.setAddressType(AddressType.valueOf(request.getAddressType().toUpperCase()));
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid address type provided, keeping existing");
            }

            address.setDefault(request.isDefault());

            // If set as default, remove from others
            if (request.isDefault()) {
                User user = address.getUsers().stream().findFirst()
                        .orElseThrow(() -> new ApiException("Address not associated with any user"));

                List<Address> userAddresses = addressRepository.findByUserId(user.getUserid());
                userAddresses.forEach(addr -> {
                    if (!addr.getAddressId().equals(addressId)) {
                        addr.setDefault(false);
                    }
                });
                addressRepository.saveAll(userAddresses);
            }

            // Save updated address
            Address updatedAddress = addressRepository.save(address);
            logger.info("Address updated successfully");

            AddressDto addressDto = modelMapper.map(updatedAddress, AddressDto.class);

            return AddressResponse.builder()
                    .success(true)
                    .message("Address updated successfully")
                    .timestamp(LocalDateTime.now())
                    .address(addressDto)
                    .status("200 OK")
                    .build();

        } catch (ResourceNotFoundException e) {
            logger.error("Error updating address: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error while updating address", e);
            throw new ApiException("Error updating address: " + e.getMessage());
        }
    }

    @Override
    public AddressResponse deleteAddress(Long addressId) {
        try {
            logger.info("Deleting address with ID: {}", addressId);

            Address address = addressRepository.findById(addressId)
                    .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

            AddressDto addressDto = modelMapper.map(address, AddressDto.class);

            // Remove from users
            address.getUsers().forEach(user -> user.getAddresses().remove(address));

            // Delete address
            addressRepository.deleteById(addressId);
            logger.info("Address deleted successfully");

            return AddressResponse.builder()
                    .success(true)
                    .message("Address deleted successfully")
                    .timestamp(LocalDateTime.now())
                    .address(addressDto)
                    .status("200 OK")
                    .build();

        } catch (ResourceNotFoundException e) {
            logger.error("Error deleting address: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error while deleting address", e);
            throw new ApiException("Error deleting address: " + e.getMessage());
        }
    }

    @Override
    public AddressResponse setDefaultAddress(Long addressId) {
        try {
            logger.info("Setting address as default: {}", addressId);

            User user = userRepository.findByUserName(authUtils.loggedInUserEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "email", authUtils.loggedInUserEmail()));

            Address address = addressRepository.findById(addressId)
                    .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

            // Remove default from all user addresses
            List<Address> userAddresses = addressRepository.findByUserId(user.getUserid());
            userAddresses.forEach(addr -> addr.setDefault(false));

            // Set this address as default
            address.setDefault(true);
            Address updatedAddress = addressRepository.save(address);
            logger.info("Default address updated");

            addressRepository.saveAll(userAddresses);

            AddressDto addressDto = modelMapper.map(updatedAddress, AddressDto.class);

            return AddressResponse.builder()
                    .success(true)
                    .message("Address set as default successfully")
                    .timestamp(LocalDateTime.now())
                    .address(addressDto)
                    .status("200 OK")
                    .build();

        } catch (Exception e) {
            logger.error("Error setting default address: {}", e.getMessage());
            throw new ApiException("Error setting default address: " + e.getMessage());
        }
    }

    @Override
    public List<AddressDto> getAddressesByType(String addressType) {
        try {
            logger.info("Fetching addresses by type: {}", addressType);

            User user = userRepository.findByUserName(authUtils.loggedInUserEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "email", authUtils.loggedInUserEmail()));

            AddressType type = AddressType.valueOf(addressType.toUpperCase());
            List<Address> addresses = addressRepository.findByUserIdAndType(user.getUserid(), type);
            logger.debug("Found {} addresses of type {}", addresses.size(), addressType);

            return addresses.stream()
                    .map(address -> modelMapper.map(address, AddressDto.class))
                    .collect(Collectors.toList());

        } catch (IllegalArgumentException e) {
            logger.error("Invalid address type: {}", addressType);
            throw new ApiException("Invalid address type: " + addressType);
        } catch (Exception e) {
            logger.error("Error fetching addresses by type: {}", e.getMessage());
            throw new ApiException("Error fetching addresses: " + e.getMessage());
        }
    }
}

