package com.tugba.ecommerce.service.impl;

import com.tugba.ecommerce.dto.AddressDTO;
import com.tugba.ecommerce.entity.Address;
import com.tugba.ecommerce.entity.AddressType;
import com.tugba.ecommerce.entity.Customer;
import com.tugba.ecommerce.Exception.ResourceNotFoundException;
import com.tugba.ecommerce.repository.AddressRepository;
import com.tugba.ecommerce.repository.CustomerRepository;
import com.tugba.ecommerce.service.AddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final CustomerRepository customerRepository;

    public AddressServiceImpl(AddressRepository addressRepository, CustomerRepository customerRepository) {
        this.addressRepository = addressRepository;
        this.customerRepository = customerRepository;
    }

    private AddressDTO convertToDTO(Address address) {
        AddressDTO dto = new AddressDTO();
        dto.setId(address.getId());
        dto.setCustomerId(address.getCustomer().getId());
        dto.setStreet(address.getStreet());
        dto.setCity(address.getCity());
        dto.setState(address.getState());
        dto.setZipCode(address.getZipCode());
        dto.setCountry(address.getCountry());
        dto.setAddressType(address.getAddressType().name()); // Enum'ı String'e dönüştür
        return dto;
    }

    private Address convertToEntity(AddressDTO dto) {
        Address entity = new Address();
        // ID'yi set etmiyoruz, JPA otomatik olarak atayacak (create durumunda)
        entity.setStreet(dto.getStreet());
        entity.setCity(dto.getCity());
        entity.setState(dto.getState());
        entity.setZipCode(dto.getZipCode());
        entity.setCountry(dto.getCountry());
        entity.setAddressType(AddressType.valueOf(dto.getAddressType())); // String'i Enum'a dönüştür
        return entity;
    }

    @Override
    @Transactional
    public AddressDTO createAddress(Long customerId, AddressDTO addressDTO) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        Address address = convertToEntity(addressDTO);
        customer.addAddress(address); // Customer entity'sindeki helper metodu kullan
        Address savedAddress = addressRepository.save(address);
        return convertToDTO(savedAddress);
    }

    @Override
    public AddressDTO getAddressById(Long customerId, Long addressId) {
        Address address = addressRepository.findByIdAndCustomerId(addressId, customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId + " for customer: " + customerId));
        return convertToDTO(address);
    }

    @Override
    public List<AddressDTO> getAddressesByCustomerId(Long customerId) {
        // Müşterinin varlığını kontrol et
        customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        List<Address> addresses = addressRepository.findByCustomerId(customerId);
        return addresses.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AddressDTO updateAddress(Long customerId, Long addressId, AddressDTO addressDTO) {
        Address existingAddress = addressRepository.findByIdAndCustomerId(addressId, customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId + " for customer: " + customerId));

        existingAddress.setStreet(addressDTO.getStreet());
        existingAddress.setCity(addressDTO.getCity());
        existingAddress.setState(addressDTO.getState());
        existingAddress.setZipCode(addressDTO.getZipCode());
        existingAddress.setCountry(addressDTO.getCountry());
        existingAddress.setAddressType(AddressType.valueOf(addressDTO.getAddressType()));

        Address updatedAddress = addressRepository.save(existingAddress);
        return convertToDTO(updatedAddress);
    }

    @Override
    @Transactional
    public void deleteAddress(Long customerId, Long addressId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        Address address = addressRepository.findByIdAndCustomerId(addressId, customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId + " for customer: " + customerId));

        customer.removeAddress(address); // Customer entity'sindeki helper metodu kullan
        addressRepository.delete(address);
    }
}