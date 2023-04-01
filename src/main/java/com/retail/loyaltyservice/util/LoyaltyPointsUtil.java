package com.retail.loyaltyservice.util;

import com.retail.loyaltyservice.model.MonthlyLoyaltyPoints;
import com.retail.loyaltyservice.model.Order;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class to loyalty programme.
 */
@Slf4j
public class LoyaltyPointsUtil {

    private static final String THRESHOLD_HUNDRED = "100";

    private static final String THRESHOLD_FIFTY = "50";

    private static final String POINTS_MULTIPLIER = "2";

    /**
     * Method to calculate total loyalty points gained for total purchase amount.
     * 
     * @param totalOrderAmount
     * @return loyalty points
     */
    public static int calculatePoints(BigDecimal totalOrderAmount) {
        log.debug("Calculating Loyalty points for the order amount : " + totalOrderAmount);
        BigDecimal thresholdHundredDollars = new BigDecimal(THRESHOLD_HUNDRED);
        BigDecimal thresholdFiftyDollars = new BigDecimal(THRESHOLD_FIFTY);

        int pointsForSpendingOver100 = totalOrderAmount.subtract(thresholdHundredDollars)
                .max(BigDecimal.ZERO)
                .multiply(new BigDecimal(POINTS_MULTIPLIER))
                .intValue();

        int pointsForSpendingOver50 = totalOrderAmount.subtract(thresholdFiftyDollars)
                .max(BigDecimal.ZERO)
                .min(thresholdFiftyDollars)
                .intValue();
        int totalLoyaltyPointsEarned = pointsForSpendingOver100 + pointsForSpendingOver50;
        log.info("Total Loyalty points earned is for this order: " + totalLoyaltyPointsEarned);
        return totalLoyaltyPointsEarned;
    }

    /**
     * Method to calculate monthly loyalty points for a customer.
     * @param orders
     * @param startDate
     * @param endDate
     * @return List<MonthlyLoyaltyPoints>
     */
    public static List<MonthlyLoyaltyPoints> getMonthlyLoyaltyPoints(List<Order> orders, LocalDate startDate, LocalDate endDate) {
        Map<YearMonth, Integer> monthlyPointsMap = new HashMap<>();
        orders.stream().forEach(order -> {
            LocalDate orderDate = order.getOrderDate();
            if(orderDate.isBefore(startDate) || orderDate.isAfter(endDate)) {
                return;
            }
            int loyaltyPoints = order.getLoyaltyPoints();
            monthlyPointsMap.merge(YearMonth.from(orderDate), loyaltyPoints, Integer::sum);
        });

        List<MonthlyLoyaltyPoints> monthlyLoyaltyPointsList = new ArrayList<>();
        for(YearMonth yearMonth : monthlyPointsMap.keySet()) {
            int loyaltyPoints = monthlyPointsMap.get(yearMonth);
            MonthlyLoyaltyPoints monthlyLoyaltyPoints = MonthlyLoyaltyPoints.builder().month(yearMonth.getMonthValue()).year(yearMonth.getYear()).points(loyaltyPoints).build();
            monthlyLoyaltyPointsList.add(monthlyLoyaltyPoints);
        }
       return monthlyLoyaltyPointsList;
    }
}
