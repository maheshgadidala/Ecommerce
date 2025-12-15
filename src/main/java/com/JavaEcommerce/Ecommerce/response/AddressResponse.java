package com.JavaEcommerce.Ecommerce.response;

import com.JavaEcommerce.Ecommerce.payload.AddressDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponse {

    private boolean success;
    private String message;
    private LocalDateTime timestamp;
    private AddressDto address;
    private String status;
}

