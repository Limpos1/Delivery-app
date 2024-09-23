package com.sparta.delivery.orders.repository;

import com.sparta.delivery.orders.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
}
