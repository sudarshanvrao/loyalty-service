package com.retail.loyaltyservice.service;

import com.retail.loyaltyservice.model.Customer;
import com.retail.loyaltyservice.model.Order;
import com.retail.loyaltyservice.repository.CustomerRepository;
import com.retail.loyaltyservice.repository.OrderRepository;
import com.retail.loyaltyservice.util.LoyaltyPointsUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Order API service implementation.
 */
@Service
@Slf4j
@Transactional
public class OrderService {

    private final OrderRepository orderRepo;

    private final CustomerRepository customerRepo;

    @Value("${loyalty.service.spend_multiplier_over_fifty}")
    private int spendMultiplierOverFifty;

    @Value("${loyalty.service.spend_multiplier_over_hundred}")
    private int spendMultiplierOverHundred;

    public OrderService(OrderRepository orderRepo, CustomerRepository customerRepo) {
        this.orderRepo = orderRepo;
        this.customerRepo = customerRepo;
    }

    /**
     * Method to create order data.
     * 
     * @param order
     * @return Order
     */
    public Order create(Order order) {
        Optional<Customer> customerOptional = customerRepo.findById(order.getCustomer().getId());
        if (customerOptional.isEmpty()) {
            throw new EntityNotFoundException("Customer not found for given Id: " + order.getCustomer().getId());
        }
        Customer customer = customerOptional.get();
        order.setCustomer(customer);

        Integer loyaltyPoints = LoyaltyPointsUtil.calculatePoints(order.getTotalAmount(), spendMultiplierOverFifty,
                spendMultiplierOverHundred);
        log.debug("Earned " + loyaltyPoints + " Loyalty points");
        order.setLoyaltyPoints(loyaltyPoints);

        Order createdOrder = orderRepo.save(order);
        customer.setLoyaltyPoints(customer.getLoyaltyPoints() + loyaltyPoints);
        customerRepo.save(customer);
        return createdOrder;
    }

    /**
     * Fetches order by orderId.
     * 
     * @param orderId
     * @return Order
     */
    public Order get(Long orderId) {
        Optional<Order> order = orderRepo.findById(orderId);
        if (order.isEmpty()) {
            throw new EntityNotFoundException("Order not found for given Id: " + orderId);
        }
        log.debug("Fetched Order details for Customer Name : " + order.get().getCustomer().getName());
        return order.get();
    }

    /**
     * Fetches orders by customerId.
     * 
     * @param customerId
     * @return List<Order>
     */
    public List<Order> getByCustomerId(Long customerId) {
        Optional<Customer> customer = customerRepo.findById(customerId);
        if (customer.isEmpty()) {
            throw new EntityNotFoundException("Customer not found for given Id: " + customerId);
        }
        List<Order> orders = orderRepo.findByCustomer(customer.get());
        if (orders.isEmpty()) {
            throw new EntityNotFoundException("Orders not found for given Customer Id: " + customerId);
        }
        log.debug("Fetched Order details for Customer Name : " + customer.get().getName());
        return orders;
    }
}
