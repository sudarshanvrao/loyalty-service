package com.retail.loyaltyservice.service;

import com.retail.loyaltyservice.model.Customer;
import com.retail.loyaltyservice.model.LoyaltySummary;
import com.retail.loyaltyservice.model.MonthlyLoyaltyPoints;
import com.retail.loyaltyservice.model.Order;
import com.retail.loyaltyservice.repository.CustomerRepository;
import com.retail.loyaltyservice.repository.OrderRepository;
import com.retail.loyaltyservice.util.LoyaltyPointsUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Loyalty API service implementation.
 */
@Service
@Slf4j
@Transactional
public class LoyaltyService {

    private final CustomerRepository customerRepo;

    private final OrderRepository orderRepo;

    public LoyaltyService(CustomerRepository customerRepo, OrderRepository orderRepo) {
        this.customerRepo = customerRepo;
        this.orderRepo = orderRepo;
    }

    /**
     * Method to get a summary of loyalty points of given customer.
     *
     * @param customerId
     * @return loyalty points
     */
    public int getLoyaltyPoints(Long customerId) {
        Optional<Customer> customer = customerRepo.findById(customerId);
        if (customer.isEmpty()) {
            throw new EntityNotFoundException("Customer not found for given Id: " + customerId);
        }
        int totalLoyaltyPoints = customer.get().getLoyaltyPoints();
        log.debug("Total Loyalty points available for customer is " + totalLoyaltyPoints);
        return totalLoyaltyPoints;
    }

    /**
     * Method to get a summary of loyalty points of given customer for given date
     * range.
     * 
     * @param customerId
     * @param startDate
     * @param endDate
     * @return LoyaltySummary
     */
    public LoyaltySummary getLoyaltySummary(Long customerId, LocalDate startDate, LocalDate endDate) {
        Optional<Customer> customerOptional = customerRepo.findById(customerId);
        if (customerOptional.isEmpty()) {
            throw new EntityNotFoundException("Customer not found for given Id: " + customerId);
        }
        Customer customer = customerOptional.get();
        if (customer.getLoyaltyPoints() == 0) {
            throw new EntityNotFoundException("No Loyalty points awarded for customer yet");
        }
        List<Order> orders = orderRepo.findByCustomer(customer);
        log.debug("Fetching Loyalty points for Customer : " + customerOptional.get().getName());
        List<MonthlyLoyaltyPoints> monthlyLoyaltyPoints = LoyaltyPointsUtil.getMonthlyLoyaltyPoints(orders, startDate,
                endDate);
        log.debug("Total Loyalty points is " + customer.getLoyaltyPoints());
        return LoyaltySummary.builder()
                .customer(customer)
                .monthlyLoyaltyPoints(monthlyLoyaltyPoints)
                .totalPoints(customer.getLoyaltyPoints())
                .build();
    }

}
