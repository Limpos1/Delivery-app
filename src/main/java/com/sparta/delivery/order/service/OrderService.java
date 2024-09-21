package com.sparta.delivery.order.service;

import com.sparta.delivery.order.dto.OrderDetailDto;
import com.sparta.delivery.order.dto.OrderRequestDto;
import com.sparta.delivery.order.dto.OrderResponseDto;
import com.sparta.delivery.order.entity.CombineDto;
import com.sparta.delivery.order.entity.OrderDetail;
import com.sparta.delivery.order.entity.Orders;
import com.sparta.delivery.order.enums.OrderStatus;
import com.sparta.delivery.order.repository.OrderDetailRepository;
import com.sparta.delivery.order.repository.OrderRepository;
import com.sparta.delivery.restorant.entity.Restaurant;
import com.sparta.delivery.restorant.repository.RestaurantRepository;
import com.sparta.delivery.user.entity.User;
import com.sparta.delivery.user.enums.UserRole;
import com.sparta.delivery.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.sparta.delivery.order.enums.OrderStatus.PENDING;
import static com.sparta.delivery.user.enums.UserRole.OWNER;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    public OrderService(OrderRepository orderRepository, OrderDetailRepository orderDetailRepository, UserRepository userRepository, RestaurantRepository restaurantRepository) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
    }
    public ResponseEntity<CombineDto> orderrequest(OrderRequestDto req) {
        LocalDateTime ordertime = LocalDateTime.now();
        User user = userRepository.findById(req.getUserId()).orElse(null);
        Restaurant rest = restaurantRepository.findById(req.getRestaurantId()).orElse(null);
        LocalDateTime openTime = rest.getOpenTime();
        LocalDateTime closeTime = rest.getCloseTime();


        if(ordertime.isBefore(openTime) || ordertime.isAfter(closeTime)){
            throw new IllegalArgumentException("영업 시간이 아닙니다.");
        }

        if(user==null){
            throw new IllegalArgumentException("User not found");
        }
        if(rest==null){
            throw new IllegalArgumentException("Restaurant not found");
        }
        Long price = req.getPrice();
        if(price<rest.getMinOrderAmount()){
            throw new IllegalArgumentException("최소 주문 금액 이상이어야 합니다.");
        }

        Long userId = user.getId();
        String name = user.getName();
        String address = req.getAddress();
        OrderStatus orderstatus = PENDING;
        Long menuid = req.getMenuId();
        Long restaurantid = req.getRestaurantId();
        Long count = 1L; //지금은 1개의 메뉴만 가능하기 때문에 메뉴의 개수는 1개로 고정


        Orders orders = new Orders(user, address, name, ordertime,orderstatus);
        OrderDetail orderdetail = new OrderDetail(orders, menuid,restaurantid,count, price);
        orderRepository.save(orders);
        orderDetailRepository.save(orderdetail);

        Long orderid = orderdetail.getOrdersId().getId();

        //orders Dto
        OrderResponseDto orderDto = new OrderResponseDto(userId, address, name, ordertime, orderstatus);
        //OrderDetail Dto
        OrderDetailDto detailDto = new OrderDetailDto(orderid, menuid, restaurantid, count,price);


        CombineDto resDto = new CombineDto(orderDto, detailDto);

        return ResponseEntity.ok(resDto);

    }

    public ResponseEntity<CombineDto> getOrder(long id) {
        //주문자 id와 조회 요청을 하는 사람의 id가 같은지 확인하는 부분을 만들것인가?

        //주문을 찾아서 저장 없다면 예외처리
        Orders order = orderRepository.findById(id).orElse(null);
        if(order==null){
            throw new IllegalArgumentException("Order not found");
        }

        //상세 주문을 찾아서 저장
        OrderDetail orderDetail = orderDetailRepository.findByOrdersId(order);

        //주문 정보를 Dto에 담기위한 과정
        Long userId = order.getUserId().getId();
        String address = order.getAddress();
        String name = order.getName();
        LocalDateTime orderTime = order.getOrderTime();
        OrderStatus status = order.getStatus();

        //상세 정보를 Dto에 담기위한 과정
        Long orderId = orderDetail.getOrdersId().getId();
        Long menuid = orderDetail.getMenuId();
        Long restaurantid = orderDetail.getRestaurantId();
        Long count = orderDetail.getCount();
        Long price = orderDetail.getPrice();

        // Dto에 데이터 저장
        OrderResponseDto orderDto = new OrderResponseDto(userId, address, name, orderTime,status);
        OrderDetailDto detailDto = new OrderDetailDto(orderId, menuid, restaurantid, count, price);

        CombineDto resDto = new CombineDto(orderDto,detailDto);


        return ResponseEntity.ok(resDto);
    }

    public OrderStatus updateOrder(Long orderid, OrderStatus oEnum) {
        Orders order = orderRepository.findById(orderid).orElse(null);
        User user = order.getUserId();
        if(user.getRole()!=OWNER){
            throw new IllegalArgumentException("가게 사장님만 변경할 수 있습니다..");
        }
        if(order==null){
            throw new IllegalArgumentException("Order not found");
        }
        if(user==null){
            throw new IllegalArgumentException("User not found");
        }
        try{
            order.setStatus(oEnum);
            orderRepository.save(order);
        }catch(Exception e){
            throw new IllegalArgumentException("잘못된 요청입니다.");
        }
        return order.getStatus();
    }
}
