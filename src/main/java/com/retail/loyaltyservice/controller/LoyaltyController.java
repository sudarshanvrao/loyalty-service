package com.retail.loyaltyservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.retail.loyaltyservice.model.LoyaltySummary;
import com.retail.loyaltyservice.service.LoyaltyService;

import java.time.LocalDate;

import static org.springframework.http.ResponseEntity.ok;

/**
 * Controller class to handle Customer Loyalty programme.
 */
@RestController
@RequestMapping("/api/v1/loyalty")
public class LoyaltyController {

    private final LoyaltyService loyaltyService;

    public LoyaltyController(LoyaltyService loyaltyService) {
        this.loyaltyService = loyaltyService;
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<Integer> getLoyaltyPoints(@PathVariable Long customerId) {
        int loyaltyPoints = loyaltyService.getLoyaltyPoints(customerId);
        return ok(loyaltyPoints);
    }

    @GetMapping(path = { "summary" })
    public ResponseEntity<LoyaltySummary> getLoyaltySummary(@RequestParam Long customerId,
            @RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        LoyaltySummary loyaltySummary = loyaltyService.getLoyaltySummary(customerId, startDate, endDate);
        return ok(loyaltySummary);
    }
}
