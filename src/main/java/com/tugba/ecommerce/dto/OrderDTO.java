package com.tugba.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;
    private Long customerId;
    private LocalDateTime orderDate;
    private String orderStatus; // Enum değeri String olarak
    private Long shippingAddressId;
    private BigDecimal totalPrice;
    private List<OrderItemDTO> orderItems;
}