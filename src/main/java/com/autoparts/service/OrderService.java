package com.autoparts.service;

import com.autoparts.dto.OrderRequest;
import com.autoparts.entity.Order;
import com.autoparts.entity.User;

import java.util.List;

public interface OrderService {
    Order createOrder(User user, OrderRequest request);
    List<Order> getUserOrders(User user);
    Order getOrderById(Long id);
    Order updateOrderStatus(Long id, Order.OrderStatus status);
    List<Order> getAllOrders();
}
