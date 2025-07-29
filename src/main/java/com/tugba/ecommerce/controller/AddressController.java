package com.tugba.ecommerce.controller;

import com.tugba.ecommerce.dto.AddressDTO;
import com.tugba.ecommerce.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers/{customerId}/addresses")
@CrossOrigin(origins = "http://localhost:3000")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping
    public ResponseEntity<AddressDTO> createAddress(@PathVariable Long customerId, @Valid @RequestBody AddressDTO addressDTO) {
        AddressDTO createdAddress = addressService.createAddress(customerId, addressDTO);
        return new ResponseEntity<>(createdAddress, HttpStatus.CREATED);
    }

    @GetMapping("/{addressId}")
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable Long customerId, @PathVariable Long addressId) {
        AddressDTO address = addressService.getAddressById(customerId, addressId);
        return ResponseEntity.ok(address);
    }

    @GetMapping
    public ResponseEntity<List<AddressDTO>> getAddressesByCustomerId(@PathVariable Long customerId) {
        List<AddressDTO> addresses = addressService.getAddressesByCustomerId(customerId);
        return ResponseEntity.ok(addresses);
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<AddressDTO> updateAddress(@PathVariable Long customerId, @PathVariable Long addressId, @Valid @RequestBody AddressDTO addressDTO) {
        AddressDTO updatedAddress = addressService.updateAddress(customerId, addressId, addressDTO);
        return ResponseEntity.ok(updatedAddress);
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long customerId, @PathVariable Long addressId) {
        addressService.deleteAddress(customerId, addressId);
        return ResponseEntity.noContent().build();
    }
}