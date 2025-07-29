package com.tugba.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {
    private Long id;
    private Long customerId; // Adresin ait olduğu müşteri ID'si
    @NotBlank(message = "Street cannot be blank")
    @Size(max = 255, message = "Street cannot exceed 255 characters")
    private String street;
    @NotBlank(message = "City cannot be blank")
    @Size(max = 100, message = "City cannot exceed 100 characters")
    private String city;
    @NotBlank(message = "State cannot be blank")
    @Size(max = 100, message = "State cannot exceed 100 characters")
    private String state;
    @NotBlank(message = "Zip code cannot be blank")
    @Size(max = 20, message = "Zip code cannot exceed 20 characters")
    private String zipCode;
    @NotBlank(message = "Country cannot be blank")
    @Size(max = 100, message = "Country cannot exceed 100 characters")
    private String country;
    @NotBlank(message = "Address type cannot be blank")
    private String addressType; // Örneğin "SHIPPING" veya "BILLING"
}