package com.retail.loyaltyservice.repository;

import com.retail.loyaltyservice.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Modifying
    @Query("UPDATE Customer c SET c.name = :name, c.email = :email, c.address = :address, c.loyaltyPoints = :loyaltyPoints WHERE c.id = :id")
    void update(@Param("id") Long id, @Param("name") String name, @Param("email") String email, @Param("address") String address, @Param("loyaltyPoints") int loyaltyPoints);
}
