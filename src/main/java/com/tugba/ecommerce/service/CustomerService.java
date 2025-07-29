package com.tugba.ecommerce.service;

import com.tugba.ecommerce.dto.CustomerDTO;
import java.util.List;

public interface CustomerService {
    CustomerDTO createCustomer(CustomerDTO customerDTO);
    CustomerDTO getCustomerById(Long id);
    List<CustomerDTO> getAllCustomers();
    CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO);
    void deleteCustomer(Long id);
}