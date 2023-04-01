package com.retail.loyaltyservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.retail.loyaltyservice.model.Order;
import com.retail.loyaltyservice.service.OrderService;

import java.util.List;

import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.ok;

/**
 * Controller class to handle Orders.
 */
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> create(@RequestBody Order order) {
        return created(null)
                .body(orderService.create(order));
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<Order> get(@PathVariable(value = "id") Long orderId) {
        Order order = orderService.get(orderId);
        return ok(order);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Order>> getByCustomerId(@PathVariable(value = "customerId") Long customerId) {
        List<Order> orders = orderService.getByCustomerId(customerId);
        return ok(orders);
    }
}
