package com.tugba.ecommerce.service.impl;

import com.tugba.ecommerce.dto.CartDTO;
import com.tugba.ecommerce.dto.CartItemDTO;
import com.tugba.ecommerce.entity.Cart;
import com.tugba.ecommerce.entity.CartItem;
import com.tugba.ecommerce.entity.Customer;
import com.tugba.ecommerce.entity.Product;
import com.tugba.ecommerce.Exception.EmptyCartException;
import com.tugba.ecommerce.Exception.OutOfStockException;
import com.tugba.ecommerce.Exception.ResourceNotFoundException;
import com.tugba.ecommerce.repository.CartItemRepository;
import com.tugba.ecommerce.repository.CartRepository;
import com.tugba.ecommerce.repository.CustomerRepository;
import com.tugba.ecommerce.repository.ProductRepository;
import com.tugba.ecommerce.service.CartService;
import com.tugba.ecommerce.service.ProductService; // ProductService import'u
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final ProductService productService;

    public CartServiceImpl(CartRepository cartRepository,
                           CartItemRepository cartItemRepository,
                           ProductRepository productRepository,
                           CustomerRepository customerRepository,
                           ProductService productService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.productService = productService;
    }

    // Entity'den DTO'ya dönüştürme metodu
    private CartDTO convertToDTO(Cart cart) {
        CartDTO dto = new CartDTO();
        dto.setId(cart.getId());
        dto.setCustomerId(cart.getCustomer().getId());
        dto.setItems(cart.getCartItems().stream().map(this::convertItemToDTO).collect(Collectors.toList()));
        dto.setTotalCartPrice(calculateCartTotalPrice(cart.getCustomer().getId()));
        return dto;
    }

    // CartItem Entity'den CartItemDTO'ya dönüştürme metodu
    private CartItemDTO convertItemToDTO(CartItem item) {
        CartItemDTO dto = new CartItemDTO();
        dto.setId(item.getId());
        dto.setProductId(item.getProduct().getId());
        dto.setProductName(item.getProduct().getName());
        dto.setProductPrice(item.getPrice()); // BigDecimal olarak
        dto.setQuantity(item.getQuantity());
        dto.setTotalPrice(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))); // BigDecimal olarak
        return dto;
    }

    @Override
    public CartDTO getCartByCustomerId(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
        Cart cart = customer.getCart();
        if (cart == null) {
            cart = new Cart();
            cart.setCustomer(customer);
            customer.setCart(cart);
            cart = cartRepository.save(cart);
        }
        return convertToDTO(cart);
    }

    @Override
    @Transactional
    public CartDTO addProductToCart(Long customerId, CartItemDTO cartItemDTO) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
        Cart cart = customer.getCart();

        if (cart == null) {
            cart = new Cart();
            cart.setCustomer(customer);
            customer.setCart(cart);
            cart = cartRepository.save(cart);
        }

        Product product = productRepository.findById(cartItemDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + cartItemDTO.getProductId()));

        Optional<CartItem> existingCartItemOptional = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());

        int quantityToAdd = cartItemDTO.getQuantity();
        if (quantityToAdd <= 0) {
            throw new IllegalArgumentException("Quantity must be at least 1.");
        }

        if (product.getStockQuantity() < quantityToAdd) { // getStockQuantity() doğru kullanılıyor
            throw new OutOfStockException("Not enough stock for product: " + product.getName() + ". Available: " + product.getStockQuantity() + ", Requested: " + quantityToAdd);
        }

        CartItem cartItem;
        if (existingCartItemOptional.isPresent()) {
            cartItem = existingCartItemOptional.get();
            int newQuantity = cartItem.getQuantity() + quantityToAdd;

            if (product.getStockQuantity() < newQuantity) {
                throw new OutOfStockException("Not enough stock for product: " + product.getName() + ". Available: " + product.getStockQuantity() + ", Requested: " + newQuantity);
            }
            cartItem.setQuantity(newQuantity);
            cartItem.setPrice(product.getPrice());
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantityToAdd);
            cartItem.setPrice(product.getPrice());
            cart.addCartItem(cartItem);
        }

        productService.decreaseStock(product.getId(), quantityToAdd);

        cartItemRepository.save(cartItem);
        return convertToDTO(cart);
    }

    @Override
    @Transactional
    public CartDTO updateCartItemQuantity(Long customerId, Long productId, Integer newQuantity) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
        Cart cart = customer.getCart();

        if (cart == null) {
            throw new EmptyCartException("Customer does not have a cart.");
        }

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + productId + " not found in cart."));

        Product product = cartItem.getProduct();

        int oldQuantity = cartItem.getQuantity();
        int quantityDifference = newQuantity - oldQuantity;

        if (newQuantity <= 0) {
            removeProductFromCart(customerId, productId);
            return getCartByCustomerId(customerId);
        }

        if (quantityDifference > 0) {
            if (product.getStockQuantity() < quantityDifference) {
                throw new OutOfStockException("Not enough stock for product: " + product.getName() + ". Available: " + product.getStockQuantity() + ", Requested increase: " + quantityDifference);
            }
            productService.decreaseStock(product.getId(), quantityDifference);
        } else if (quantityDifference < 0) {
            productService.increaseStock(product.getId(), Math.abs(quantityDifference));
        }

        cartItem.setQuantity(newQuantity);
        cartItemRepository.save(cartItem);
        return convertToDTO(cart);
    }

    @Override
    @Transactional
    public void removeProductFromCart(Long customerId, Long productId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
        Cart cart = customer.getCart();

        if (cart == null) {
            throw new EmptyCartException("Customer does not have a cart.");
        }

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + productId + " not found in cart."));

        int quantityToReturn = cartItem.getQuantity();
        productService.increaseStock(productId, quantityToReturn);

        cart.removeCartItem(cartItem);
        cartItemRepository.delete(cartItem);
    }

    @Override
    @Transactional
    public void clearCart(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
        Cart cart = customer.getCart();

        if (cart == null || cart.getCartItems().isEmpty()) {
            throw new EmptyCartException("Cart is already empty or does not exist for customer: " + customerId);
        }

        for (CartItem item : cart.getCartItems()) {
            productService.increaseStock(item.getProduct().getId(), item.getQuantity());
        }

        cart.getCartItems().clear();
        cartRepository.save(cart);
    }

    @Override
    public BigDecimal calculateCartTotalPrice(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
        Cart cart = customer.getCart();

        if (cart == null || cart.getCartItems().isEmpty()) {
            return BigDecimal.ZERO;
        }

        return cart.getCartItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}