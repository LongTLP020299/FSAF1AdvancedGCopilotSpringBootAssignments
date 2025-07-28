package com.fsoft.ecommerce.service.impl;

import com.fsoft.ecommerce.dto.CreateOrderRequestDTO;
import com.fsoft.ecommerce.dto.OrderDTO;
import com.fsoft.ecommerce.entity.Order;
import com.fsoft.ecommerce.entity.OrderItem;
import com.fsoft.ecommerce.entity.OrderStatus;
import com.fsoft.ecommerce.entity.Product;
import com.fsoft.ecommerce.entity.User;
import com.fsoft.ecommerce.exception.InsufficientStockException;
import com.fsoft.ecommerce.exception.ProductNotFoundException;
import com.fsoft.ecommerce.exception.UserNotFoundException;
import com.fsoft.ecommerce.repository.OrderRepository;
import com.fsoft.ecommerce.repository.ProductRepository;
import com.fsoft.ecommerce.repository.UserRepository;
import com.fsoft.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Override
    public Page<OrderDTO> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(this::toDTO);
    }

    @Override
    public Optional<OrderDTO> getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(this::toDTO);
    }

    @Override
    public OrderDTO createOrder(OrderDTO orderDTO) {
        Order order = toEntity(orderDTO);
        order.setOrderDate(LocalDateTime.now());
        Order saved = orderRepository.save(order);
        return toDTO(saved);
    }

    @Override
    public Optional<OrderDTO> updateOrder(Long id, OrderDTO orderDTO) {
        return orderRepository.findById(id)
                .map(existingOrder -> {
                    updateEntityFromDTO(existingOrder, orderDTO);
                    Order updated = orderRepository.save(existingOrder);
                    return toDTO(updated);
                });
    }

    @Override
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    @Override
    public void cancelOrder(Long id) {
        orderRepository.findById(id).ifPresent(order -> {
            order.setStatus(com.fsoft.ecommerce.entity.OrderStatus.CANCELLED);
            orderRepository.save(order);
        });
    }

    @Override
    public Page<OrderDTO> getOrdersByUserId(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable)
                .map(this::toDTO);
    }

    private OrderDTO toDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setUserId(order.getUser().getId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus().toString());
        dto.setOrderDate(order.getOrderDate());
        dto.setShippingAddress(order.getShippingAddress());
        return dto;
    }

    private Order toEntity(OrderDTO dto) {
        Order order = new Order();
        // Note: User will need to be set separately as we only have userId
        order.setTotalAmount(dto.getTotalAmount());
        if (dto.getStatus() != null) {
            order.setStatus(com.fsoft.ecommerce.entity.OrderStatus.valueOf(dto.getStatus()));
        }
        order.setShippingAddress(dto.getShippingAddress());
        return order;
    }

    private void updateEntityFromDTO(Order order, OrderDTO dto) {
        if (dto.getTotalAmount() != null) order.setTotalAmount(dto.getTotalAmount());
        if (dto.getStatus() != null) order.setStatus(com.fsoft.ecommerce.entity.OrderStatus.valueOf(dto.getStatus()));
        if (dto.getShippingAddress() != null) order.setShippingAddress(dto.getShippingAddress());
    }



    @Override
    public boolean canPlaceOrder(Long productId, Integer quantity) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            return false;
        }
        Product product = productOpt.get();
        return quantity > 0 && product.getStockQuantity() >= quantity;
    }

    @Override
    public List<OrderDTO> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status).stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, OrderStatus newStatus) {
        return orderRepository.findById(orderId)
                .map(order -> {
                    order.setStatus(newStatus);
                    Order updated = orderRepository.save(order);
                    return toDTO(updated);
                })
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
    }

    @Override
    @Transactional
    public void confirmOrder(Long orderId) {
        updateOrderStatus(orderId, OrderStatus.CONFIRMED);
    }

    @Override
    @Transactional
    public void shipOrder(Long orderId) {
        updateOrderStatus(orderId, OrderStatus.SHIPPED);
    }

    @Override
    @Transactional
    public void deliverOrder(Long orderId) {
        updateOrderStatus(orderId, OrderStatus.DELIVERED);
    }
    @Override
    @Transactional
    public Order placeOrder(CreateOrderRequestDTO request) {
        // 1. Validate toàn bộ order request
        OrderValidationContext validationContext = validateOrderRequest(request);
        
        // 2. Reserve inventory với proper error handling
        InventoryReservation reservation = reserveInventory(
            validationContext.getProduct(), 
            request.getQuantity()
        );
        
        try {
            // 3. Build complete order entity
            Order order = buildOrder(request, validationContext);
            
            // 4. Persist order và commit inventory
            return finalizeOrderPlacement(order, reservation);
            
        } catch (Exception e) {
            // 5. Rollback inventory reservation nếu có lỗi
            rollbackInventoryReservation(reservation);
            throw e;
        }
    }

    /**
     * Validates order request và returns validation context
     * 
     * @param request the order request to validate
     * @return validation context containing validated entities
     * @throws IllegalArgumentException if request parameters are invalid
     * @throws UserNotFoundException if user is not found
     * @throws ProductNotFoundException if product is not found
     */
    private OrderValidationContext validateOrderRequest(CreateOrderRequestDTO request) {
        // Validate basic parameters
        validateBasicOrderParameters(request);
        
        // Retrieve và validate entities
        User user = verifyUserExists(request.getUserId());
        Product product = retrieveAndValidateProduct(request.getProductId());
        
        return new OrderValidationContext(user, product);
    }

    /**
     * Validates basic order parameters
     * 
     * @param request the order request to validate
     * @throws IllegalArgumentException if any parameter is invalid
     */
    private void validateBasicOrderParameters(CreateOrderRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Order request cannot be null");
        }
        if (request.getUserId() == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (request.getProductId() == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
    }

    /**
     * Retrieves và validates user existence
     * 
     * @param userId the user ID to retrieve
     * @return the validated user entity
     * @throws UserNotFoundException if user is not found
     */
    private User verifyUserExists(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
    }

    /**
     * Retrieves và validates product existence
     * 
     * @param productId the product ID to retrieve
     * @return the validated product entity
     * @throws ProductNotFoundException if product is not found
     */
    private Product retrieveAndValidateProduct(Long productId) {
        return productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
    }

    /**
     * Reserves inventory for order placement
     * 
     * @param product the product to reserve
     * @param quantity the quantity to reserve
     * @return inventory reservation details
     * @throws InsufficientStockException if insufficient stock is available
     */
    private InventoryReservation reserveInventory(Product product, Integer quantity) {
        // Validate stock availability
        validateStockAvailability(product, quantity);
        
        // Perform atomic stock reservation
        Product updatedProduct = performStockReservation(product, quantity);
        
        return new InventoryReservation(updatedProduct, quantity);
    }

    /**
     * Validates stock availability without side effects
     * 
     * @param product the product to check
     * @param quantity the required quantity
     * @throws InsufficientStockException if insufficient stock is available
     */
    private void validateStockAvailability(Product product, Integer quantity) {
        if (product.getStockQuantity() < quantity) {
            throw new InsufficientStockException(
                String.format("Insufficient stock for product: %s. Available: %d, Requested: %d", 
                    product.getName(), product.getStockQuantity(), quantity)
            );
        }
    }

    /**
     * Performs atomic stock reservation
     * 
     * @param product the product to update
     * @param quantity the quantity to reserve
     * @return the updated product entity
     */
    private Product performStockReservation(Product product, Integer quantity) {
        product.setStockQuantity(product.getStockQuantity() - quantity);
        return productRepository.save(product);
    }

    /**
     * Builds complete order entity với all relationships
     * 
     * @param request the order request
     * @param context the validation context
     * @return the complete order entity
     */
    private Order buildOrder(CreateOrderRequestDTO request, OrderValidationContext context) {
        // Initialize order foundation
        Order order = initializeOrderBase(request, context.getUser());
        
        // Calculate financial details
        BigDecimal totalAmount = calculateOrderTotal(context.getProduct(), request.getQuantity());
        order.setTotalAmount(totalAmount);
        
        // Create và attach order items
        Set<OrderItem> orderItems = createOrderItems(order, context.getProduct(), request.getQuantity());
        order.setOrderItems(orderItems);
        
        return order;
    }

    /**
     * Initializes order với basic information
     * 
     * @param request the order request
     * @param user the user placing the order
     * @return the initialized order entity
     */
    private Order initializeOrderBase(CreateOrderRequestDTO request, User user) {
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(request.getShippingAddress());
        return order;
    }

    /**
     * Calculates total amount for order
     * 
     * @param product the product being ordered
     * @param quantity the quantity ordered
     * @return the calculated total amount
     */
    private BigDecimal calculateOrderTotal(Product product, Integer quantity) {
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

    /**
     * Creates order items for the order
     * 
     * @param order the parent order
     * @param product the product being ordered
     * @param quantity the quantity ordered
     * @return set of order items
     */
    private Set<OrderItem> createOrderItems(Order order, Product product, Integer quantity) {
        OrderItem orderItem = createSingleOrderItem(order, product, quantity);
        
        Set<OrderItem> orderItems = new HashSet<>();
        orderItems.add(orderItem);
        return orderItems;
    }

    /**
     * Creates a single order item
     * 
     * @param order the parent order
     * @param product the product
     * @param quantity the quantity
     * @return the created order item
     */
    private OrderItem createSingleOrderItem(Order order, Product product, Integer quantity) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(quantity);
        orderItem.setPrice(product.getPrice()); // Use actual product price instead of hardcoded value
        return orderItem;
    }

    /**
     * Finalizes order placement và commits inventory
     * 
     * @param order the order to finalize
     * @param reservation the inventory reservation
     * @return the persisted order entity
     */
    private Order finalizeOrderPlacement(Order order, InventoryReservation reservation) {
        // Persist order
        Order savedOrder = persistOrder(order);
        
        // Mark inventory reservation as committed
        commitInventoryReservation(reservation);
        
        return savedOrder;
    }

    /**
     * Persists order entity
     * 
     * @param order the order to persist
     * @return the persisted order entity
     */
    private Order persistOrder(Order order) {
        return orderRepository.save(order);
    }

    /**
     * Commits inventory reservation
     * 
     * @param reservation the reservation to commit
     */
    private void commitInventoryReservation(InventoryReservation reservation) {
        // In more complex systems, this would mark reservation as committed
        // For now, stock has already been updated during reservation
        logInventoryOperation("COMMITTED", reservation);
    }

    /**
     * Rollbacks inventory reservation on failure
     * 
     * @param reservation the reservation to rollback
     */
    private void rollbackInventoryReservation(InventoryReservation reservation) {
        try {
            Product product = reservation.getProduct();
            restoreProductStock(product, reservation.getQuantity());
            logInventoryOperation("ROLLED_BACK", reservation);
        } catch (Exception e) {
            // Log error but don't throw to avoid masking original exception
            // In production, use proper logging framework
            System.err.println("Failed to rollback inventory reservation for product " + 
                reservation.getProduct().getId() + ": " + e.getMessage());
        }
    }

    /**
     * Restores product stock
     * 
     * @param product the product to restore
     * @param quantity the quantity to restore
     */
    private void restoreProductStock(Product product, Integer quantity) {
        product.setStockQuantity(product.getStockQuantity() + quantity);
        productRepository.save(product);
    }

    /**
     * Logs inventory operations for audit trail
     * 
     * @param operation the operation type
     * @param reservation the reservation details
     */
    private void logInventoryOperation(String operation, InventoryReservation reservation) {
        // TODO: Implement proper audit logging with logging framework
        System.out.println("Inventory " + operation + " for product " + 
            reservation.getProduct().getId() + " quantity " + reservation.getQuantity());
    }

    /**
     * Context object cho order validation results
     */
    private static class OrderValidationContext {
        private final User user;
        private final Product product;
        
        public OrderValidationContext(User user, Product product) {
            this.user = user;
            this.product = product;
        }
        
        public User getUser() { 
            return user; 
        }
        
        public Product getProduct() { 
            return product; 
        }
    }

    /**
     * Represents an inventory reservation
     */
    private static class InventoryReservation {
        private final Product product;
        private final Integer quantity;
        
        public InventoryReservation(Product product, Integer quantity) {
            this.product = product;
            this.quantity = quantity;
        }
        
        public Product getProduct() { 
            return product; 
        }
        
        public Integer getQuantity() { 
            return quantity; 
        }
    }
}
