package com.tugba.ecommerce.repository;

import com.tugba.ecommerce.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List; // !!! BU SATIRI EKLEYİN !!!

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    // Belirli bir sepet ve ürüne ait CartItem'ı bulmak için
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);
    // Belirli bir sepete ait tüm CartItem'ları bulmak için
    List<CartItem> findByCartId(Long cartId); // Buradaki List hataya neden oluyordu
}