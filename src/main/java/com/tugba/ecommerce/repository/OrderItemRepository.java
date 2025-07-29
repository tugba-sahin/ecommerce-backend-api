package com.tugba.ecommerce.repository;

import com.tugba.ecommerce.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // Belirli bir siparişe ait tüm sipariş öğelerini bulmak için
    // List<OrderItem> findByOrderId(Long orderId); // İhtiyaç duyulursa eklenebilir
}