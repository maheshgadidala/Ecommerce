package com.JavaEcommerce.Ecommerce.service;

import com.JavaEcommerce.Ecommerce.payload.OrderDto;
import com.JavaEcommerce.Ecommerce.request.CreateOrderRequest;
import com.JavaEcommerce.Ecommerce.response.OrderResponse;

import java.util.List;

public interface OrderService {

    // Create order from user's cart
    OrderResponse createOrder(CreateOrderRequest request);

    // Get all orders for logged-in user
    List<OrderDto> getUserOrders();

    // Get order details by ID
    OrderDto getOrderById(Long orderId);

    // Get orders by status
    List<OrderDto> getOrdersByStatus(String status);

    // Update order status
    OrderResponse updateOrderStatus(Long orderId, String status);

    // Cancel order
    OrderResponse cancelOrder(Long orderId);

    // Get order history with pagination
    List<OrderDto> getOrderHistory(int pageNumber, int pageSize);

    // Get recent orders
    List<OrderDto> getRecentOrders(int limit);
}

