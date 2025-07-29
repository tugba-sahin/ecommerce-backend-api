package com.tugba.ecommerce.controller;

import com.tugba.ecommerce.dto.OrderDTO;
import com.tugba.ecommerce.enums.OrderStatus; // Enum import'u
import com.tugba.ecommerce.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers/{customerId}/orders")
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@PathVariable Long customerId, @RequestBody Map<String, Long> payload) {
        Long shippingAddressId = payload.get("shippingAddressId");
        if (shippingAddressId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Adres ID'si zorunlu
        }
        OrderDTO createdOrder = orderService.createOrder(customerId, shippingAddressId);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getOrdersByCustomerId(@PathVariable Long customerId) {
        List<OrderDTO> orders = orderService.getOrdersByCustomerId(customerId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long customerId, @PathVariable Long orderId) {
        // Müşteri ID'sini de kontrol etmek isterseniz, servise parametre olarak geçirebilirsiniz.
        // Şimdilik sadece orderId ile kontrol edelim.
        OrderDTO order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable Long customerId, @PathVariable Long orderId, @RequestParam String newStatus) {
        OrderStatus status = OrderStatus.valueOf(newStatus.toUpperCase()); // String'i Enum'a dönüştür
        OrderDTO updatedOrder = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long customerId, @PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}