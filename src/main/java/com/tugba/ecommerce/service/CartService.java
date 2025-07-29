package com.tugba.ecommerce.service;

import com.tugba.ecommerce.dto.CartDTO;
import com.tugba.ecommerce.dto.CartItemDTO;

import java.math.BigDecimal;

public interface CartService {
    CartDTO getCartByCustomerId(Long customerId);
    CartDTO addProductToCart(Long customerId, CartItemDTO cartItemDTO);
    CartDTO updateCartItemQuantity(Long customerId, Long productId, Integer quantity);
    void removeProductFromCart(Long customerId, Long productId);
    void clearCart(Long customerId);
    BigDecimal calculateCartTotalPrice(Long customerId);
}