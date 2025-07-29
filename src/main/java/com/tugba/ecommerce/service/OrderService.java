package com.tugba.ecommerce.service;

import com.tugba.ecommerce.dto.OrderDTO;
import com.tugba.ecommerce.enums.OrderStatus; // Enum import'u
import java.util.List;

public interface OrderService {
    OrderDTO createOrder(Long customerId, Long shippingAddressId);
    List<OrderDTO> getOrdersByCustomerId(Long customerId);
    OrderDTO getOrderById(Long orderId);
    OrderDTO updateOrderStatus(Long orderId, OrderStatus newStatus);
    void deleteOrder(Long orderId);
}