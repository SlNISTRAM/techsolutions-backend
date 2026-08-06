package com.techsolutions.command.dto;

import com.techsolutions.command.Order;
import com.techsolutions.command.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de respuesta con el estado actual de un pedido.
 */
public record OrderResponse(
        String      id,
        String      customerName,
        List<String> items,
        double      totalAmount,
        double      discountApplied,
        OrderStatus status,
        LocalDateTime createdAt
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomerName(),
                order.getItems(),
                order.getTotalAmount(),
                order.getDiscount(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }
}
