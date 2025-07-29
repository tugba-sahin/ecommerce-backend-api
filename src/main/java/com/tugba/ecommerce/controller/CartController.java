package com.tugba.ecommerce.controller;

import com.tugba.ecommerce.dto.CartDTO;
import com.tugba.ecommerce.dto.CartItemDTO;
import com.tugba.ecommerce.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers/{customerId}/cart")
@CrossOrigin(origins = "http://localhost:3000")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<CartDTO> getCartByCustomerId(@PathVariable Long customerId) {
        CartDTO cart = cartService.getCartByCustomerId(customerId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/items")
    public ResponseEntity<CartDTO> addProductToCart(@PathVariable Long customerId, @Valid @RequestBody CartItemDTO cartItemDTO) {
        CartDTO updatedCart = cartService.addProductToCart(customerId, cartItemDTO);
        return new ResponseEntity<>(updatedCart, HttpStatus.OK);
    }

    @PutMapping("/items/{productId}")
    public ResponseEntity<CartDTO> updateCartItemQuantity(
            @PathVariable Long customerId,
            @PathVariable Long productId,
            @RequestParam Integer quantity) {
        CartDTO updatedCart = cartService.updateCartItemQuantity(customerId, productId, quantity);
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> removeProductFromCart(@PathVariable Long customerId, @PathVariable Long productId) {
        cartService.removeProductFromCart(customerId, productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(@PathVariable Long customerId) {
        cartService.clearCart(customerId);
        return ResponseEntity.noContent().build();
    }
}