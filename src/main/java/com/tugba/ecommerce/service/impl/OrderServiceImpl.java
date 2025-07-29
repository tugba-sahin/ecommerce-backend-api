package com.tugba.ecommerce.service.impl;

import com.tugba.ecommerce.dto.OrderDTO;
import com.tugba.ecommerce.dto.OrderItemDTO;
import com.tugba.ecommerce.entity.Address;
import com.tugba.ecommerce.entity.Cart;
import com.tugba.ecommerce.entity.CartItem;
import com.tugba.ecommerce.entity.Customer;
import com.tugba.ecommerce.entity.Order;
import com.tugba.ecommerce.entity.OrderItem;
import com.tugba.ecommerce.entity.Product;
import com.tugba.ecommerce.enums.OrderStatus; // Doğru import
import com.tugba.ecommerce.Exception.EmptyCartException;
import com.tugba.ecommerce.Exception.OutOfStockException;
import com.tugba.ecommerce.Exception.ResourceNotFoundException;
import com.tugba.ecommerce.repository.AddressRepository;
import com.tugba.ecommerce.repository.CartRepository;
import com.tugba.ecommerce.repository.CustomerRepository;
import com.tugba.ecommerce.repository.OrderItemRepository;
import com.tugba.ecommerce.repository.OrderRepository;
import com.tugba.ecommerce.service.CartService;
import com.tugba.ecommerce.service.OrderService;
import com.tugba.ecommerce.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private final CartRepository cartRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductService productService;
    private final CartService cartService;

    public OrderServiceImpl(OrderRepository orderRepository,
                            CustomerRepository customerRepository,
                            AddressRepository addressRepository,
                            CartRepository cartRepository,
                            OrderItemRepository orderItemRepository,
                            ProductService productService,
                            CartService cartService) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.addressRepository = addressRepository;
        this.cartRepository = cartRepository;
        this.orderItemRepository = orderItemRepository;
        this.productService = productService;
        this.cartService = cartService;
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setCustomerId(order.getCustomer().getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setOrderStatus(order.getOrderStatus().name());
        dto.setShippingAddressId(order.getShippingAddress().getId());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setOrderItems(order.getOrderItems().stream()
                .map(this::convertOrderItemToDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    private OrderItemDTO convertOrderItemToDTO(OrderItem orderItem) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(orderItem.getId());
        dto.setProductId(orderItem.getProduct().getId());
        dto.setProductName(orderItem.getProduct().getName());
        dto.setQuantity(orderItem.getQuantity());
        dto.setPrice(orderItem.getPrice());
        dto.setTotalPrice(orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
        return dto;
    }

    @Override
    @Transactional
    public OrderDTO createOrder(Long customerId, Long shippingAddressId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        Address shippingAddress = addressRepository.findById(shippingAddressId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipping address not found with id: " + shippingAddressId));

        Cart cart = customer.getCart();
        if (cart == null || cart.getCartItems().isEmpty()) {
            throw new EmptyCartException("Cannot create an order from an empty cart for customer: " + customerId);
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setShippingAddress(shippingAddress);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.PROCESSING);

        BigDecimal totalOrderPrice = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getCartItems()) {
            Product product = cartItem.getProduct();
            int orderedQuantity = cartItem.getQuantity();

            // Stok kontrolü (Buradaki metot getStockQuantity olmalı, getStock değil)
            if (product.getStockQuantity() < orderedQuantity) {
                throw new OutOfStockException("Not enough stock for product: " + product.getName() + ". Available: " + product.getStockQuantity());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(orderedQuantity);
            orderItem.setPrice(cartItem.getPrice());

            order.addOrderItem(orderItem);
            totalOrderPrice = totalOrderPrice.add(orderItem.getPrice().multiply(BigDecimal.valueOf(orderedQuantity)));

            // Stokları Order oluşturulurken düşürüyoruz, çünkü Cart'tan silme işleminde geri ekliyorduk.
            productService.decreaseStock(product.getId(), orderedQuantity);
        }

        order.setTotalPrice(totalOrderPrice);
        Order savedOrder = orderRepository.save(order);

        // Sipariş başarıyla oluşturulduktan sonra sepeti temizle
        cartService.clearCart(customerId);

        return convertToDTO(savedOrder);
    }

    @Override
    public List<OrderDTO> getOrdersByCustomerId(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
        List<Order> orders = orderRepository.findByCustomerId(customerId);
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDTO getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        return convertToDTO(order);
    }

    @Override
    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        order.setOrderStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);
        return convertToDTO(updatedOrder);
    }

    @Override
    @Transactional
    public void deleteOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        // Sipariş silindiğinde stokların geri gelmesini sağlıyoruz
        for (OrderItem item : order.getOrderItems()) {
            productService.increaseStock(item.getProduct().getId(), item.getQuantity());
        }

        orderRepository.delete(order);
    }
}