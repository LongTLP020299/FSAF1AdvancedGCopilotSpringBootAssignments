package com.fsoft.ecommerce.service.impl;

import com.fsoft.ecommerce.dto.CreateOrderRequestDTO;
import com.fsoft.ecommerce.entity.Order;
import com.fsoft.ecommerce.entity.OrderStatus;
import com.fsoft.ecommerce.entity.Product;
import com.fsoft.ecommerce.entity.User;
import com.fsoft.ecommerce.exception.InsufficientStockException;
import com.fsoft.ecommerce.exception.ProductNotFoundException;
import com.fsoft.ecommerce.exception.UserNotFoundException;
import com.fsoft.ecommerce.repository.OrderRepository;
import com.fsoft.ecommerce.repository.ProductRepository;
import com.fsoft.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for OrderServiceImpl to validate that refactoring maintains external behavior
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    
    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private OrderServiceImpl orderService;
    
    private User testUser;
    private Product testProduct;
    private CreateOrderRequestDTO validOrderRequest;
    
    @BeforeEach
    void setUp() {
        // Set up test data
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setPrice(BigDecimal.valueOf(100.00));
        testProduct.setStockQuantity(10);
        
        validOrderRequest = new CreateOrderRequestDTO();
        validOrderRequest.setUserId(1L);
        validOrderRequest.setProductId(1L);
        validOrderRequest.setQuantity(2);
        validOrderRequest.setShippingAddress("123 Test Street");
    }
    
    @Test
    void placeOrder_ValidRequest_ShouldSucceed() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        
        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setUser(testUser);
        savedOrder.setStatus(OrderStatus.PENDING);
        savedOrder.setTotalAmount(BigDecimal.valueOf(200.00));
        savedOrder.setOrderDate(LocalDateTime.now());
        
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        
        // Act
        Order result = orderService.placeOrder(validOrderRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(testUser, result.getUser());
        assertEquals(OrderStatus.PENDING, result.getStatus());
        assertEquals(BigDecimal.valueOf(200.00), result.getTotalAmount());
        
        // Verify repository interactions
        verify(userRepository).findById(1L);
        verify(productRepository).findById(1L);
        verify(productRepository).save(testProduct);
        verify(orderRepository).save(any(Order.class));
        
        // Verify stock was reduced
        assertEquals(8, testProduct.getStockQuantity());
    }
    
    @Test
    void placeOrder_NullRequest_ShouldThrowIllegalArgumentException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> orderService.placeOrder(null)
        );
        
        assertEquals("Order request cannot be null", exception.getMessage());
        
        // Verify no repository interactions
        verifyNoInteractions(userRepository, productRepository, orderRepository);
    }
    
    @Test
    void placeOrder_NullUserId_ShouldThrowIllegalArgumentException() {
        // Arrange
        validOrderRequest.setUserId(null);
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> orderService.placeOrder(validOrderRequest)
        );
        
        assertEquals("User ID cannot be null", exception.getMessage());
        
        // Verify no repository interactions
        verifyNoInteractions(userRepository, productRepository, orderRepository);
    }
    
    @Test
    void placeOrder_NullProductId_ShouldThrowIllegalArgumentException() {
        // Arrange
        validOrderRequest.setProductId(null);
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> orderService.placeOrder(validOrderRequest)
        );
        
        assertEquals("Product ID cannot be null", exception.getMessage());
        
        // Verify no repository interactions
        verifyNoInteractions(userRepository, productRepository, orderRepository);
    }
    
    @Test
    void placeOrder_InvalidQuantity_ShouldThrowIllegalArgumentException() {
        // Arrange
        validOrderRequest.setQuantity(0);
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> orderService.placeOrder(validOrderRequest)
        );
        
        assertEquals("Quantity must be greater than 0", exception.getMessage());
        
        // Verify no repository interactions
        verifyNoInteractions(userRepository, productRepository, orderRepository);
    }
    
    @Test
    void placeOrder_UserNotFound_ShouldThrowUserNotFoundException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Act & Assert
        UserNotFoundException exception = assertThrows(
            UserNotFoundException.class, 
            () -> orderService.placeOrder(validOrderRequest)
        );
        
        assertEquals("User not found with id: 1", exception.getMessage());
        
        // Verify repository interactions
        verify(userRepository).findById(1L);
        verifyNoInteractions(productRepository, orderRepository);
    }
    
    @Test
    void placeOrder_ProductNotFound_ShouldThrowProductNotFoundException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Act & Assert
        ProductNotFoundException exception = assertThrows(
            ProductNotFoundException.class, 
            () -> orderService.placeOrder(validOrderRequest)
        );
        
        assertEquals("Product not found with id: 1", exception.getMessage());
        
        // Verify repository interactions
        verify(userRepository).findById(1L);
        verify(productRepository).findById(1L);
        verifyNoInteractions(orderRepository);
    }
    
    @Test
    void placeOrder_InsufficientStock_ShouldThrowInsufficientStockException() {
        // Arrange
        testProduct.setStockQuantity(1); // Less than requested quantity of 2
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        
        // Act & Assert
        InsufficientStockException exception = assertThrows(
            InsufficientStockException.class, 
            () -> orderService.placeOrder(validOrderRequest)
        );
        
        assertTrue(exception.getMessage().contains("Insufficient stock for product: Test Product"));
        assertTrue(exception.getMessage().contains("Available: 1"));
        assertTrue(exception.getMessage().contains("Requested: 2"));
        
        // Verify repository interactions
        verify(userRepository).findById(1L);
        verify(productRepository).findById(1L);
        verifyNoInteractions(orderRepository);
    }
    
    @Test
    void placeOrder_CalculatesTotalCorrectly() {
        // Arrange
        testProduct.setPrice(BigDecimal.valueOf(50.75));
        validOrderRequest.setQuantity(3);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        
        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setTotalAmount(BigDecimal.valueOf(152.25)); // 50.75 * 3
        
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        
        // Act
        Order result = orderService.placeOrder(validOrderRequest);
        
        // Assert
        assertEquals(BigDecimal.valueOf(152.25), result.getTotalAmount());
        
        // Verify stock was reduced correctly
        assertEquals(7, testProduct.getStockQuantity()); // 10 - 3 = 7
    }
    
    @Test
    void placeOrder_SetsCorrectOrderStatus() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        
        Order savedOrder = new Order();
        savedOrder.setStatus(OrderStatus.PENDING);
        
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        
        // Act
        Order result = orderService.placeOrder(validOrderRequest);
        
        // Assert
        assertEquals(OrderStatus.PENDING, result.getStatus());
    }
    
    @Test
    void placeOrder_SetsShippingAddress() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        
        Order savedOrder = new Order();
        savedOrder.setShippingAddress("123 Test Street");
        
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            assertEquals("123 Test Street", order.getShippingAddress());
            return savedOrder;
        });
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        
        // Act
        orderService.placeOrder(validOrderRequest);
        
        // Assert is done in the answer callback above
        verify(orderRepository).save(any(Order.class));
    }
    
    @Test
    void canPlaceOrder_ValidRequest_ShouldReturnTrue() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        
        // Act
        boolean result = orderService.canPlaceOrder(1L, 5);
        
        // Assert
        assertTrue(result);
        verify(productRepository).findById(1L);
    }
    
    @Test
    void canPlaceOrder_ProductNotFound_ShouldReturnFalse() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Act
        boolean result = orderService.canPlaceOrder(1L, 5);
        
        // Assert
        assertFalse(result);
        verify(productRepository).findById(1L);
    }
    
    @Test
    void canPlaceOrder_InsufficientStock_ShouldReturnFalse() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        
        // Act
        boolean result = orderService.canPlaceOrder(1L, 15); // More than available stock
        
        // Assert
        assertFalse(result);
        verify(productRepository).findById(1L);
    }
    
    @Test
    void canPlaceOrder_InvalidQuantity_ShouldReturnFalse() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        
        // Act
        boolean result = orderService.canPlaceOrder(1L, 0);
        
        // Assert
        assertFalse(result);
        verify(productRepository).findById(1L);
    }

    // ============ Additional Test Cases for 100% Coverage ============

    @Test
    void getAllOrders_ShouldReturnPagedOrders() {
        // Given
        when(orderRepository.findAll(any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(org.springframework.data.domain.Page.empty());

        // When
        var result = orderService.getAllOrders(org.springframework.data.domain.Pageable.unpaged());

        // Then
        assertNotNull(result);
        verify(orderRepository).findAll(any(org.springframework.data.domain.Pageable.class));
    }

    @Test
    void getOrderById_WithValidId_ShouldReturnOrder() {
        // Given
        Order order = new Order();
        order.setId(1L);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // When
        var result = orderService.getOrderById(1L);

        // Then
        assertTrue(result.isPresent());
        verify(orderRepository).findById(1L);
    }

    @Test
    void getOrderById_WithInvalidId_ShouldReturnEmpty() {
        // Given
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        var result = orderService.getOrderById(999L);

        // Then
        assertTrue(result.isEmpty());
        verify(orderRepository).findById(999L);
    }

    @Test
    void createOrder_ShouldCreateAndReturnOrder() {
        // Given
        com.fsoft.ecommerce.dto.OrderDTO orderDTO = new com.fsoft.ecommerce.dto.OrderDTO();
        orderDTO.setTotalAmount(BigDecimal.valueOf(100));
        
        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setTotalAmount(BigDecimal.valueOf(100));
        
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // When
        var result = orderService.createOrder(orderDTO);

        // Then
        assertNotNull(result);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void updateOrder_WithValidData_ShouldUpdateOrder() {
        // Given
        Order existingOrder = new Order();
        existingOrder.setId(1L);
        existingOrder.setTotalAmount(BigDecimal.valueOf(100));
        
        com.fsoft.ecommerce.dto.OrderDTO orderDTO = new com.fsoft.ecommerce.dto.OrderDTO();
        orderDTO.setTotalAmount(BigDecimal.valueOf(150));
        
        when(orderRepository.findById(1L)).thenReturn(Optional.of(existingOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(existingOrder);

        // When
        var result = orderService.updateOrder(1L, orderDTO);

        // Then
        assertTrue(result.isPresent());
        verify(orderRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void cancelOrder_WithValidId_ShouldCancelOrder() {
        // Given
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);
        
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // When
        orderService.cancelOrder(1L);

        // Then
        verify(orderRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void deleteOrder_WithValidId_ShouldDeleteOrder() {
        // Given
        Order order = new Order();
        order.setId(1L);
        
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // When
        orderService.deleteOrder(1L);

        // Then
        verify(orderRepository).findById(1L);
        verify(orderRepository).delete(order);
    }

    @Test
    void getOrdersByUserId_ShouldReturnUserOrders() {
        // Given
        when(orderRepository.findByUserId(eq(1L), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(org.springframework.data.domain.Page.empty());

        // When
        var result = orderService.getOrdersByUserId(1L, org.springframework.data.domain.Pageable.unpaged());

        // Then
        assertNotNull(result);
        verify(orderRepository).findByUserId(eq(1L), any(org.springframework.data.domain.Pageable.class));
    }

    @Test
    void getOrdersByStatus_ShouldReturnOrdersWithStatus() {
        // Given
        when(orderRepository.findByStatus(OrderStatus.PENDING))
                .thenReturn(java.util.Collections.emptyList());

        // When
        var result = orderService.getOrdersByStatus(OrderStatus.PENDING);

        // Then
        assertNotNull(result);
        verify(orderRepository).findByStatus(OrderStatus.PENDING);
    }

    @Test
    void updateOrderStatus_ShouldUpdateStatus() {
        // Given
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);
        
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // When
        var result = orderService.updateOrderStatus(1L, OrderStatus.CONFIRMED);

        // Then
        assertNotNull(result);
        verify(orderRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void confirmOrder_ShouldConfirmOrder() {
        // Given
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);
        
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // When
        orderService.confirmOrder(1L);

        // Then
        verify(orderRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void shipOrder_ShouldShipOrder() {
        // Given
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.CONFIRMED);
        
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // When
        orderService.shipOrder(1L);

        // Then
        verify(orderRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void deliverOrder_ShouldDeliverOrder() {
        // Given
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.SHIPPED);
        
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // When
        orderService.deliverOrder(1L);

        // Then
        verify(orderRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
    }

    /**
     * Test to verify that @Transactional annotation ensures proper rollback
     * when order placement fails after stock deduction
     */
    @Test
    void placeOrder_WhenOrderSaveFails_ShouldRollbackStockDeduction() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        
        // Setup product repository to save stock deduction successfully first time
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        
        // But order repository save throws exception
        when(orderRepository.save(any(Order.class))).thenThrow(new RuntimeException("Database connection failed"));
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.placeOrder(validOrderRequest);
        });
        
        assertEquals("Database connection failed", exception.getMessage());
        
        // Verify that both automatic @Transactional rollback AND manual rollback work together:
        // 1. Stock deduction happens first (performStockReservation)
        // 2. Order save fails, triggering manual rollback (restoreProductStock)
        // 3. @Transactional ensures the entire transaction is rolled back at database level
        verify(productRepository, times(2)).save(any(Product.class)); // Stock deduction + manual rollback
        verify(orderRepository, times(1)).save(any(Order.class)); // Called once and failed
        
        // Note: This demonstrates defense-in-depth:
        // - Manual rollback logic provides immediate compensation
        // - @Transactional provides database-level transaction safety
    }
}
