package com.fsoft.ecommerce.repository;

import com.fsoft.ecommerce.entity.Order;
import com.fsoft.ecommerce.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
    Page<Order> findByUserId(Long userId, Pageable pageable);
    List<Order> findByStatus(OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.status = :status")
    List<Order> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") OrderStatus status);
    
    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END FROM Order o " +
           "JOIN o.orderItems oi WHERE o.user.id = :userId AND oi.product.id = :productId AND o.status = :status")
    boolean existsByUserIdAndOrderItemsProductIdAndStatus(@Param("userId") Long userId, 
                                                         @Param("productId") Long productId, 
                                                         @Param("status") OrderStatus status);
    
    // Efficient native SQL query to fetch multiple dashboard statistics in one database call
    @Query(value = "SELECT " +
                  "  COALESCE(SUM(CASE WHEN o.status = 'DELIVERED' THEN oi.price * oi.quantity ELSE 0 END), 0) AS totalRevenue, " +
                  "  COUNT(DISTINCT o.id) AS totalOrders, " +
                  "  COUNT(DISTINCT CASE WHEN MONTH(u.created_at) = MONTH(CURRENT_DATE()) AND YEAR(u.created_at) = YEAR(CURRENT_DATE()) THEN u.id END) AS newCustomersThisMonth " +
                  "FROM orders o " +
                  "LEFT JOIN order_items oi ON o.id = oi.order_id " +
                  "LEFT JOIN users u ON o.user_id = u.id",
          nativeQuery = true)
    Map<String, Object> getDashboardStats();
}
