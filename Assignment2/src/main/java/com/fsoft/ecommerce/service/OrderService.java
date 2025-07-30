package com.fsoft.ecommerce.service;

import com.fsoft.ecommerce.dto.CreateOrderRequestDTO;
import com.fsoft.ecommerce.dto.OrderDTO;
import com.fsoft.ecommerce.entity.Order;
import com.fsoft.ecommerce.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    Page<OrderDTO> getAllOrders(Pageable pageable);
    Optional<OrderDTO> getOrderById(Long id);
    OrderDTO createOrder(OrderDTO orderDTO);
    Optional<OrderDTO> updateOrder(Long id, OrderDTO orderDTO);
    void deleteOrder(Long id);
    void cancelOrder(Long id);
    Page<OrderDTO> getOrdersByUserId(Long userId, Pageable pageable);
    
    // Enhanced placeOrder functionality
    Order placeOrder(CreateOrderRequestDTO request);
    boolean canPlaceOrder(Long productId, Integer quantity);
    List<OrderDTO> getOrdersByStatus(OrderStatus status);
    OrderDTO updateOrderStatus(Long orderId, OrderStatus newStatus);
    void confirmOrder(Long orderId);
    void shipOrder(Long orderId);
    void deliverOrder(Long orderId);
}
