package com.tugba.ecommerce.service.impl;

import com.tugba.ecommerce.dto.CustomerDTO;
import com.tugba.ecommerce.entity.Cart;
import com.tugba.ecommerce.entity.Customer;
import com.tugba.ecommerce.Exception.ResourceNotFoundException;
import com.tugba.ecommerce.repository.CartRepository;
import com.tugba.ecommerce.repository.CustomerRepository;
import com.tugba.ecommerce.service.CustomerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CartRepository cartRepository; // Sepet repository'sini ekledik

    public CustomerServiceImpl(CustomerRepository customerRepository, CartRepository cartRepository) {
        this.customerRepository = customerRepository;
        this.cartRepository = cartRepository;
    }

    // Entity'den DTO'ya dönüştürme metodu
    private CustomerDTO convertToDTO(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        dto.setEmail(customer.getEmail());
        return dto;
    }

    // DTO'dan Entity'ye dönüştürme metodu
    private Customer convertToEntity(CustomerDTO dto) {
        Customer entity = new Customer();
        // ID'yi set etmiyoruz, JPA otomatik olarak atayacak (create durumunda)
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());
        return entity;
    }

    @Override
    @Transactional
    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        // E-posta zaten mevcut mu kontrol et
        if (customerRepository.findByEmail(customerDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Customer with this email already exists: " + customerDTO.getEmail());
        }

        Customer customer = convertToEntity(customerDTO);
        // Yeni müşteri için boş bir sepet oluştur ve ilişkilendir
        Cart newCart = new Cart();
        newCart.setCustomer(customer); // Müşteriyi sepete ata
        customer.setCart(newCart); // Sepeti müşteriye ata

        Customer savedCustomer = customerRepository.save(customer);
        cartRepository.save(newCart); // Sepeti de kaydet (customer.setCart zaten cascade ile kaydedebilir ama emin olalım)

        return convertToDTO(savedCustomer);
    }

    @Override
    public CustomerDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        return convertToDTO(customer);
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        // E-posta güncelleniyorsa ve zaten mevcutsa kontrol et (kendi e-postası hariç)
        if (!existingCustomer.getEmail().equals(customerDTO.getEmail())) {
            if (customerRepository.findByEmail(customerDTO.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Customer with this email already exists: " + customerDTO.getEmail());
            }
        }

        existingCustomer.setFirstName(customerDTO.getFirstName());
        existingCustomer.setLastName(customerDTO.getLastName());
        existingCustomer.setEmail(customerDTO.getEmail());

        Customer updatedCustomer = customerRepository.save(existingCustomer);
        return convertToDTO(updatedCustomer);
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        customerRepository.delete(customer);
    }
}