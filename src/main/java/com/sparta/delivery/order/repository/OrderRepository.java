package com.sparta.delivery.order.repository;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.sparta.delivery.order.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Orders, Long> {

}
