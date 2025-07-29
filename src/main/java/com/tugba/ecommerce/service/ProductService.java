package com.tugba.ecommerce.service;

import com.tugba.ecommerce.dto.ProductDTO;
import java.util.List;

public interface ProductService {
    ProductDTO createProduct(ProductDTO productDTO);
    ProductDTO getProductById(Long id);
    List<ProductDTO> getAllProducts();
    ProductDTO updateProduct(Long id, ProductDTO productDTO);
    void deleteProduct(Long id);
    void decreaseStock(Long productId, Integer quantity); // Stok azaltma
    void increaseStock(Long productId, Integer quantity); // Stok artırma
}