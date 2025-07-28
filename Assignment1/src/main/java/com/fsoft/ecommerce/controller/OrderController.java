package com.fsoft.ecommerce.controller;

import com.fsoft.ecommerce.dto.CreateOrderRequestDTO;
import com.fsoft.ecommerce.dto.OrderDTO;
import com.fsoft.ecommerce.entity.Order;
import com.fsoft.ecommerce.entity.OrderStatus;
import com.fsoft.ecommerce.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Operation(summary = "Get all orders", description = "Get paginated list of all orders")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of orders returned successfully")
    })
    @GetMapping
    public Page<OrderDTO> getAllOrders(Pageable pageable) {
        return orderService.getAllOrders(pageable);
    }

    @Operation(summary = "Get order by ID", description = "Get an order by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Order found"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create order", description = "Create a new order")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Order created successfully")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDTO createOrder(@RequestBody OrderDTO dto) {
        return orderService.createOrder(dto);
    }

    @Operation(summary = "Update order", description = "Update an existing order by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Order updated successfully"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<OrderDTO> updateOrder(@PathVariable Long id, @RequestBody OrderDTO dto) {
        return orderService.updateOrder(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete order", description = "Delete an order by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Order deleted successfully")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Cancel order", description = "Cancel an order by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Order cancelled successfully"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id) {
        try {
            orderService.cancelOrder(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    // New enhanced placeOrder endpoints
    @Operation(summary = "Place order", description = "Place a new order with product and quantity")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Order placed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request or insufficient stock"),
        @ApiResponse(responseCode = "404", description = "Product or user not found")
    })
    @PostMapping("/place")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Order> placeOrder(@Valid @RequestBody CreateOrderRequestDTO request) {
        try {
            Order order = orderService.placeOrder(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Check if order can be placed", description = "Check if an order can be placed for given product and quantity")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Check completed")
    })
    @GetMapping("/can-place")
    public ResponseEntity<Boolean> canPlaceOrder(
            @RequestParam Long productId, 
            @RequestParam Integer quantity) {
        boolean canPlace = orderService.canPlaceOrder(productId, quantity);
        return ResponseEntity.ok(canPlace);
    }

    @Operation(summary = "Get orders by status", description = "Get all orders with specific status")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Orders returned successfully")
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderDTO>> getOrdersByStatus(@PathVariable OrderStatus status) {
        List<OrderDTO> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Update order status", description = "Update the status of an order")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Order status updated successfully"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long id, 
            @RequestParam OrderStatus status) {
        try {
            OrderDTO updatedOrder = orderService.updateOrderStatus(id, status);
            return ResponseEntity.ok(updatedOrder);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Confirm order", description = "Confirm a pending order")
    @PostMapping("/{id}/confirm")
    public ResponseEntity<Void> confirmOrder(@PathVariable Long id) {
        try {
            orderService.confirmOrder(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Ship order", description = "Mark order as shipped")
    @PostMapping("/{id}/ship")
    public ResponseEntity<Void> shipOrder(@PathVariable Long id) {
        try {
            orderService.shipOrder(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Deliver order", description = "Mark order as delivered")
    @PostMapping("/{id}/deliver")
    public ResponseEntity<Void> deliverOrder(@PathVariable Long id) {
        try {
            orderService.deliverOrder(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
