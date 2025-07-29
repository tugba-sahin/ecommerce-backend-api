package com.tugba.ecommerce.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "carts")
public class Cart extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false) // İlişkiyi customer_id sütunu üzerinden kurar
    private Customer customer;

    // CartItem'lar ile OneToMany ilişkisi.
    // cascade = CascadeType.ALL: Cart silindiğinde CartItem'lar da silinir.
    // orphanRemoval = true: Bir CartItem, Cart'tan ayrılırsa (ilişkisi kesilirse) veritabanından silinir.
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    // Helper metotlar (liste yönetimini kolaylaştırır ve ilişkiyi iki taraflı tutar)
    public void addCartItem(CartItem item) {
        cartItems.add(item);
        item.setCart(this);
    }

    public void removeCartItem(CartItem item) {
        cartItems.remove(item);
        item.setCart(null); // İlişkiyi kes
    }
}