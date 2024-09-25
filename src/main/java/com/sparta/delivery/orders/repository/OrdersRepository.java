package com.sparta.delivery.orders.repository;

import com.sparta.delivery.orders.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {

    List<Orders> findAllByRestaurantId(Long restaurantId);
}
