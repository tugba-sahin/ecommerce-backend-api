package com.tugba.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@MappedSuperclass // Bu sınıfın veritabanı tablosu oluşturmayacağını, diğer entity'ler tarafından miras alınacağını belirtir
@Data // Getter, Setter, toString, equals, hashCode otomatik oluşturur
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp // Kayıt oluşturulduğunda otomatik olarak zaman damgası ekler
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp // Kayıt güncellendiğinde otomatik olarak zaman damgası ekler
    @Column(nullable = false)
    private LocalDateTime updatedDate;
}