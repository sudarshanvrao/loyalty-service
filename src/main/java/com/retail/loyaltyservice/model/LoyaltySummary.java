package com.retail.loyaltyservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Type for Loyalty summary details of a customer for given month range.
 */
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LoyaltySummary {

    private Customer customer;
    private List<MonthlyLoyaltyPoints> monthlyLoyaltyPoints;
    private long totalPoints;
}
