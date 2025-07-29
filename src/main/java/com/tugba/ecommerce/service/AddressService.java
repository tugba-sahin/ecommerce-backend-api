package com.tugba.ecommerce.service;

import com.tugba.ecommerce.dto.AddressDTO;
import java.util.List;

public interface AddressService {
    AddressDTO createAddress(Long customerId, AddressDTO addressDTO);
    AddressDTO getAddressById(Long customerId, Long addressId);
    List<AddressDTO> getAddressesByCustomerId(Long customerId);
    AddressDTO updateAddress(Long customerId, Long addressId, AddressDTO addressDTO);
    void deleteAddress(Long customerId, Long addressId);
}