package com.sparta.delivery.restorant.repository;

import com.sparta.delivery.order.entity.OrderDetail;
import com.sparta.delivery.restorant.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository  extends JpaRepository<Restaurant, Long> {

}
