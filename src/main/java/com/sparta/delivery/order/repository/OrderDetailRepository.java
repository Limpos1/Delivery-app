package com.sparta.delivery.order.repository;


import com.sparta.delivery.orders.entity.OrderDetail;
import com.sparta.delivery.orders.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    OrderDetail findByOrdersId(Orders order);
}
