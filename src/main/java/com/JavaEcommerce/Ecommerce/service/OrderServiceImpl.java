package com.JavaEcommerce.Ecommerce.service;

import com.JavaEcommerce.Ecommerce.exception.ApiException;
import com.JavaEcommerce.Ecommerce.exception.ResourceNotFoundException;
import com.JavaEcommerce.Ecommerce.model.*;
import com.JavaEcommerce.Ecommerce.payload.OrderDto;
import com.JavaEcommerce.Ecommerce.payload.OrderItemDto;
import com.JavaEcommerce.Ecommerce.repo.*;
import com.JavaEcommerce.Ecommerce.request.CreateOrderRequest;
import com.JavaEcommerce.Ecommerce.response.OrderResponse;
import com.JavaEcommerce.Ecommerce.util.AuthUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartRepo cartRepository;

    @Autowired
    private CartItemRepo cartItemRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthUtils authUtils;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public OrderResponse createOrder(CreateOrderRequest request) {
        try {
            logger.info("Creating order with request");

            // Get logged-in user
            User user = userRepository.findByUserName(authUtils.loggedInUserEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "email", authUtils.loggedInUserEmail()));

            logger.debug("User found: {}", user.getUserEmail());

            // Get user's cart
            Cart cart = cartRepository.findByUserUserEmail(user.getUserEmail());
            if (cart == null || cart.getCartItems().isEmpty()) {
                throw new ApiException("Cart is empty. Cannot create order");
            }

            logger.debug("Cart found with {} items", cart.getCartItems().size());

            // Get shipping address
            Address shippingAddress = addressRepository.findById(request.getAddressId())
                    .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", request.getAddressId()));

            logger.debug("Shipping address found: {}", shippingAddress.getAddressId());

            // Create order
            Order order = new Order();
            order.setUser(user);
            order.setShippingAddress(shippingAddress);
            order.setOrderStatus(OrderStatus.PENDING);
            order.setOrderNotes(request.getOrderNotes());
            order.setDiscountAmount(request.getDiscountAmount() != null ? request.getDiscountAmount() : 0.0);

            // Calculate totals
            Double totalAmount = 0.0;

            // Add cart items as order items
            for (CartsItem cartItem : cart.getCartItems()) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setProduct(cartItem.getProduct());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setPricePerUnit(cartItem.getProduct().getSpecialPrice());
                orderItem.setDiscount(0.0);

                Double itemTotal = orderItem.getPricePerUnit() * orderItem.getQuantity();
                orderItem.setItemTotal(itemTotal);

                order.getOrderItems().add(orderItem);
                totalAmount += itemTotal;

                logger.debug("Added order item: Product ID {}, Qty: {}, Price: {}",
                    cartItem.getProduct().getProductId(), orderItem.getQuantity(), itemTotal);
            }

            // Set order amounts
            order.setTotalAmount(totalAmount);
            order.setFinalAmount(totalAmount - order.getDiscountAmount());
            order.setEstimatedDeliveryDate(LocalDateTime.now().plusDays(7));

            // Save order
            Order savedOrder = orderRepository.save(order);
            logger.info("Order created successfully with ID: {}", savedOrder.getOrderId());

            // Clear cart after order creation
            for (CartsItem cartItem : new java.util.ArrayList<>(cart.getCartItems())) {
                cartItemRepository.delete(cartItem);
            }
            cart.setTotalPrice(0.0);
            cartRepository.save(cart);
            logger.debug("Cart cleared after order creation");

            // Convert to DTO and return
            OrderDto orderDto = convertToOrderDto(savedOrder);

            return OrderResponse.builder()
                    .success(true)
                    .message("Order created successfully")
                    .timestamp(LocalDateTime.now())
                    .order(orderDto)
                    .status("201 CREATED")
                    .build();

        } catch (ResourceNotFoundException | ApiException e) {
            logger.error("Error creating order: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error while creating order", e);
            throw new ApiException("Error creating order: " + e.getMessage());
        }
    }

    @Override
    public List<OrderDto> getUserOrders() {
        try {
            logger.info("Fetching orders for user");

            User user = userRepository.findByUserName(authUtils.loggedInUserEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "email", authUtils.loggedInUserEmail()));

            List<Order> orders = orderRepository.findByUserUserid(user.getUserid());
            logger.debug("Found {} orders", orders.size());

            return orders.stream()
                    .map(this::convertToOrderDto)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            logger.error("Error fetching user orders: {}", e.getMessage());
            throw new ApiException("Error fetching orders: " + e.getMessage());
        }
    }

    @Override
    public OrderDto getOrderById(Long orderId) {
        try {
            logger.info("Fetching order by ID: {}", orderId);

            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new ResourceNotFoundException("Order", "orderId", orderId));

            logger.debug("Order found");
            return convertToOrderDto(order);

        } catch (Exception e) {
            logger.error("Error fetching order: {}", e.getMessage());
            throw new ApiException("Error fetching order: " + e.getMessage());
        }
    }

    @Override
    public List<OrderDto> getOrdersByStatus(String status) {
        try {
            logger.info("Fetching orders by status: {}", status);

            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            List<Order> orders = orderRepository.findByOrderStatus(orderStatus);
            logger.debug("Found {} orders with status {}", orders.size(), status);

            return orders.stream()
                    .map(this::convertToOrderDto)
                    .collect(Collectors.toList());

        } catch (IllegalArgumentException e) {
            logger.error("Invalid order status: {}", status);
            throw new ApiException("Invalid order status: " + status);
        } catch (Exception e) {
            logger.error("Error fetching orders by status: {}", e.getMessage());
            throw new ApiException("Error fetching orders: " + e.getMessage());
        }
    }

    @Override
    public OrderResponse updateOrderStatus(Long orderId, String status) {
        try {
            logger.info("Updating order status for order ID: {}", orderId);

            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new ResourceNotFoundException("Order", "orderId", orderId));

            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            order.setOrderStatus(orderStatus);

            // If status is DELIVERED, set delivery date
            if (orderStatus == OrderStatus.DELIVERED) {
                order.setDeliveryDate(LocalDateTime.now());
            }

            Order updatedOrder = orderRepository.save(order);
            logger.info("Order status updated to: {}", status);

            OrderDto orderDto = convertToOrderDto(updatedOrder);

            return OrderResponse.builder()
                    .success(true)
                    .message("Order status updated successfully")
                    .timestamp(LocalDateTime.now())
                    .order(orderDto)
                    .status("200 OK")
                    .build();

        } catch (IllegalArgumentException e) {
            logger.error("Invalid order status: {}", status);
            throw new ApiException("Invalid order status: " + status);
        } catch (Exception e) {
            logger.error("Error updating order status: {}", e.getMessage());
            throw new ApiException("Error updating order: " + e.getMessage());
        }
    }

    @Override
    public OrderResponse cancelOrder(Long orderId) {
        try {
            logger.info("Cancelling order with ID: {}", orderId);

            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new ResourceNotFoundException("Order", "orderId", orderId));

            // Check if order can be cancelled
            if (order.getOrderStatus() == OrderStatus.DELIVERED || order.getOrderStatus() == OrderStatus.CANCELLED) {
                throw new ApiException("Cannot cancel " + order.getOrderStatus() + " order");
            }

            order.setOrderStatus(OrderStatus.CANCELLED);
            Order cancelledOrder = orderRepository.save(order);
            logger.info("Order cancelled successfully");

            OrderDto orderDto = convertToOrderDto(cancelledOrder);

            return OrderResponse.builder()
                    .success(true)
                    .message("Order cancelled successfully")
                    .timestamp(LocalDateTime.now())
                    .order(orderDto)
                    .status("200 OK")
                    .build();

        } catch (Exception e) {
            logger.error("Error cancelling order: {}", e.getMessage());
            throw new ApiException("Error cancelling order: " + e.getMessage());
        }
    }

    @Override
    public List<OrderDto> getOrderHistory(int pageNumber, int pageSize) {
        try {
            logger.info("Fetching order history - Page: {}, Size: {}", pageNumber, pageSize);

            User user = userRepository.findByUserName(authUtils.loggedInUserEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "email", authUtils.loggedInUserEmail()));

            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            // Note: This requires additional repository method for pagination
            List<Order> orders = orderRepository.findByUserUserid(user.getUserid());
            logger.debug("Found {} orders in history", orders.size());

            return orders.stream()
                    .map(this::convertToOrderDto)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            logger.error("Error fetching order history: {}", e.getMessage());
            throw new ApiException("Error fetching order history: " + e.getMessage());
        }
    }

    @Override
    public List<OrderDto> getRecentOrders(int limit) {
        try {
            logger.info("Fetching {} recent orders", limit);

            User user = userRepository.findByUserName(authUtils.loggedInUserEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "email", authUtils.loggedInUserEmail()));

            List<Order> orders = orderRepository.findRecentOrdersByUser(user.getUserid());
            logger.debug("Found {} recent orders", orders.size());

            return orders.stream()
                    .limit(limit)
                    .map(this::convertToOrderDto)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            logger.error("Error fetching recent orders: {}", e.getMessage());
            throw new ApiException("Error fetching recent orders: " + e.getMessage());
        }
    }

    private OrderDto convertToOrderDto(Order order) {
        OrderDto orderDto = new OrderDto();
        orderDto.setOrderId(order.getOrderId());
        orderDto.setUserId(order.getUser().getUserid());
        orderDto.setUserName(order.getUser().getUserName());
        orderDto.setUserEmail(order.getUser().getUserEmail());
        orderDto.setAddressId(order.getShippingAddress().getAddressId());
        orderDto.setShippingAddressDetails(order.getShippingAddress().getStreet() + ", " +
                order.getShippingAddress().getCity() + ", " +
                order.getShippingAddress().getState() + ", " +
                order.getShippingAddress().getCountry());
        orderDto.setOrderStatus(order.getOrderStatus().toString());
        orderDto.setTotalAmount(order.getTotalAmount());
        orderDto.setDiscountAmount(order.getDiscountAmount());
        orderDto.setFinalAmount(order.getFinalAmount());
        orderDto.setOrderDate(order.getOrderDate());
        orderDto.setEstimatedDeliveryDate(order.getEstimatedDeliveryDate());
        orderDto.setDeliveryDate(order.getDeliveryDate());
        orderDto.setOrderNotes(order.getOrderNotes());

        List<OrderItemDto> orderItemDtos = order.getOrderItems().stream()
                .map(item -> {
                    OrderItemDto itemDto = new OrderItemDto();
                    itemDto.setOrderItemId(item.getOrderItemId());
                    itemDto.setProductId(item.getProduct().getProductId());
                    itemDto.setProductName(item.getProduct().getProductName());
                    itemDto.setQuantity(item.getQuantity());
                    itemDto.setPricePerUnit(item.getPricePerUnit());
                    itemDto.setDiscount(item.getDiscount());
                    itemDto.setItemTotal(item.getItemTotal());
                    return itemDto;
                })
                .collect(Collectors.toList());

        orderDto.setOrderItems(orderItemDtos);
        return orderDto;
    }
}

