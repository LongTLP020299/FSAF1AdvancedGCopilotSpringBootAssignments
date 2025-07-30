package com.fsoft.ecommerce.service.impl;

import com.fsoft.ecommerce.entity.OrderStatus;
import com.fsoft.ecommerce.repository.OrderRepository;
import com.fsoft.ecommerce.service.PurchaseVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PurchaseVerificationServiceImpl implements PurchaseVerificationService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Override
    public boolean hasUserPurchasedProduct(Long userId, Long productId) {
        return orderRepository.existsByUserIdAndOrderItemsProductIdAndStatus(
            userId, productId, OrderStatus.DELIVERED);
    }
}
