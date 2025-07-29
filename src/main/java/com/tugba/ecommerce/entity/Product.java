package com.tugba.ecommerce.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal; // BigDecimal import'u

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "products")
public class Product extends BaseEntity { // BaseEntity'den miras alır
    private String name;
    private String description;
    @Column(precision = 10, scale = 2) // Fiyat için BigDecimal ve hassasiyet ayarı
    private BigDecimal price;
    private Integer stockQuantity; // Stok miktarı
    private String imageUrl;
}