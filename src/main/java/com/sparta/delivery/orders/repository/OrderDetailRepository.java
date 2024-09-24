package com.sparta.delivery.orders.repository;


import com.sparta.delivery.orders.entity.OrderDetail;
import com.sparta.delivery.orders.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    OrderDetail findByOrdersId(Orders order);

    List<OrderDetail> findAllByOrdersId(Orders order);
}
