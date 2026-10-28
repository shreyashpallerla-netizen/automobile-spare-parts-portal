package com.autoparts.service.impl;

import com.autoparts.dto.OrderRequest;
import com.autoparts.entity.*;
import com.autoparts.exception.ResourceNotFoundException;
import com.autoparts.exception.StockException;
import com.autoparts.repository.*;
import com.autoparts.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public Order createOrder(User user, OrderRequest request) {
        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cannot create order: cart is empty");
        }

        Order order = Order.builder()
            .user(user)
            .status(Order.OrderStatus.PENDING)
            .shippingAddress(request.getShippingAddress())
            .items(new ArrayList<>())
            .build();

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            // Validate stock
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new StockException("Insufficient stock for: " + product.getName() +
                    ". Available: " + product.getStockQuantity() +
                    ", Requested: " + cartItem.getQuantity());
            }

            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            OrderItem orderItem = OrderItem.builder()
                .order(order)
                .product(product)
                .quantity(cartItem.getQuantity())
                .unitPrice(product.getPrice())
                .subtotal(subtotal)
                .build();

            order.getItems().add(orderItem);
            total = total.add(subtotal);

            // Deduct stock
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);
        }

        order.setTotalAmount(total);
        Order savedOrder = orderRepository.save(order);

        // Clear cart
        cartItemRepository.deleteByUser(user);

        log.info("Order created: id={} user={} total={}", savedOrder.getId(), user.getUsername(), total);
        return savedOrder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getUserOrders(User user) {
        return orderRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    @Override
    public Order updateOrderStatus(Long id, Order.OrderStatus status) {
        Order order = getOrderById(id);
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        Order updated = orderRepository.save(order);
        log.info("Order {} status updated to {}", id, status);
        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }
}
