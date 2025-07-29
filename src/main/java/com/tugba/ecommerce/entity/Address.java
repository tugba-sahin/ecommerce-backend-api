package com.tugba.ecommerce.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true) // BaseEntity'nin hashCode/equals'ini dahil et
@Table(name = "addresses")
public class Address extends BaseEntity { // BaseEntity'den miras alır
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;

    @Enumerated(EnumType.STRING) // Enum'ı String olarak kaydet
    @Column(nullable = false)
    private AddressType addressType; // SHIPPING veya BILLING

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
}