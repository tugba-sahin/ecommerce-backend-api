package com.tugba.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {
    private Long id;
    private Long customerId;
    private List<CartItemDTO> items; // List olarak tanımlı
    private BigDecimal totalCartPrice; // BigDecimal olarak tanımlı
}