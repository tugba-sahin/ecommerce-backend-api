package com.tugba.ecommerce.enums;

public enum OrderStatus {

    PENDING,        // Sipariş beklemede
    PROCESSING,     // Sipariş işleniyor
    SHIPPED,        // Sipariş kargoya verildi
    DELIVERED,      // Sipariş teslim edildi
    CANCELLED       // Sipariş iptal edildi
}