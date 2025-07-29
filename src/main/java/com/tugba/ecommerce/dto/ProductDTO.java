package com.tugba.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal; // BigDecimal import'u

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id;
    @NotBlank(message = "Product name cannot be blank")
    @Size(max = 255, message = "Product name cannot exceed 255 characters")
    private String name;
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price; // BigDecimal
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;
    private String imageUrl;
}