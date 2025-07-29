package com.tugba.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal; // BigDecimal import'u

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    private Long id;
    @NotNull(message = "Product ID cannot be null")
    private Long productId;
    private String productName;
    private BigDecimal productPrice; // BigDecimal
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    private BigDecimal totalPrice; // BigDecimal
}