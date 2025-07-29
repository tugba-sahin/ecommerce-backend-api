package com.tugba.ecommerce.repository;

import com.tugba.ecommerce.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByCustomerId(Long customerId);
    Optional<Address> findByIdAndCustomerId(Long id, Long customerId);
}