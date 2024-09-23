package com.sparta.delivery.order.service;

import com.sparta.delivery.order.dto.OrderDetailDto;
import com.sparta.delivery.order.dto.OrderRequestDto;
import com.sparta.delivery.order.dto.OrderResponseDto;
import com.sparta.delivery.order.dto.CombineDto;
import com.sparta.delivery.order.repository.OrderDetailRepository;
import com.sparta.delivery.order.repository.OrderRepository;
import com.sparta.delivery.orders.entity.OrderDetail;
import com.sparta.delivery.orders.entity.Orders;
import com.sparta.delivery.orders.enums.OrderStatus;
import com.sparta.delivery.restaurant.entity.Restaurant;
import com.sparta.delivery.restorant.repository.RestaurantRepository;
import com.sparta.delivery.user.entity.User;
import com.sparta.delivery.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import static com.sparta.delivery.orders.enums.OrderStatus.PENDING;
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
    public ResponseEntity<CombineDto> orderrequest(Long userId, OrderRequestDto req) {
        LocalDateTime ordertime = LocalDateTime.now();
        User user = userRepository.findById(userId).orElse(null);
        Restaurant rest = restaurantRepository.findById(req.getRestaurantId()).orElseThrow(()->new IllegalArgumentException("등록된 식당이 없습니다."));


        LocalDateTime openTime = rest.getOpenTime();
        LocalDateTime closeTime = rest.getCloseTime();
        if(rest==null){
            throw new IllegalArgumentException("Restaurant not found");
        }
        if(ordertime.isBefore(openTime) || ordertime.isAfter(closeTime)){
            throw new IllegalArgumentException("영업 시간이 아닙니다.");
        }

        if(user==null){
            throw new IllegalArgumentException("User not found");
        }

        Long price = req.getPrice();
        if(price<rest.getMinOrderAmount()){
            throw new IllegalArgumentException("최소 주문 금액 이상이어야 합니다.");
        }
        String name = user.getName();
        String address = req.getAddress();
        OrderStatus orderstatus = PENDING;
        Long menuid = req.getMenuId();
        Long restaurantid = req.getRestaurantId();
        Long count = 1L; //지금은 1개의 메뉴만 가능하기 때문에 메뉴의 개수는 1개로 고정


        Orders orders = new Orders(user, address, name,rest,ordertime, orderstatus);
        OrderDetail orderdetail = new OrderDetail(orders, menuid,restaurantid,count, price,ordertime);
        orderRepository.save(orders);
        orderDetailRepository.save(orderdetail);

        Long orderid = orderdetail.getOrdersId().getId();

        //orders Dto
        OrderResponseDto orderDto = new OrderResponseDto(userId, address, name, ordertime, orderstatus);
        //OrderDetail Dto
        OrderDetailDto detailDto = new OrderDetailDto(orderid, menuid, restaurantid, count,price,ordertime);


        CombineDto resDto = new CombineDto(orderDto, detailDto);

        return ResponseEntity.ok(resDto);

    }

    public ResponseEntity<CombineDto> getOrder(long id) {

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
        LocalDateTime orderTime = orderDetail.getOrderTime();
        OrderStatus status = order.getStatus();

        //상세 정보를 Dto에 담기위한 과정
        Long orderId = orderDetail.getOrdersId().getId();
        Long menuid = orderDetail.getMenuId();
        Long restaurantid = orderDetail.getRestaurantId();
        Long count = orderDetail.getCount();
        Long price = orderDetail.getPrice();

        // Dto에 데이터 저장
        OrderResponseDto orderDto = new OrderResponseDto(userId, address, name, orderTime,status);
        OrderDetailDto detailDto = new OrderDetailDto(orderId, menuid, restaurantid, count, price,orderTime);

        CombineDto resDto = new CombineDto(orderDto,detailDto);


        return ResponseEntity.ok(resDto);
    }

    public OrderStatus updateOrder(Long userId, Long orderid, OrderStatus oEnum) {
        Orders order = orderRepository.findById(orderid).orElse(null);
        OrderDetail orderDetail = orderDetailRepository.findByOrdersId(order);
        LocalDateTime modifyNow = LocalDateTime.now();
        User user = userRepository.findById(userId).orElse(null);
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
            orderDetail.setModifiedAt(modifyNow);
            order.setStatus(oEnum);
            orderRepository.save(order);
      }catch(Exception e){
            throw new IllegalArgumentException("잘못된 요청입니다.");
      }
        return order.getStatus();
    }

    public ResponseEntity<List<OrderResponseDto>> getOrderFromRest(Long userId, Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new IllegalArgumentException("등록된 식당이 없습니다."));
        User user = restaurant.getOwnerId();
        if(!Objects.equals(user.getId(), userId)){
            throw new IllegalArgumentException("해당 가게의 사장님만 조회가능합니다.");
        }
        List<Orders> orders = orderRepository.findAllByRestaurantId(restaurantId);

        // 각 주문을 OrderResponseDto로 변환하여 리스트 생성
        List<OrderResponseDto> orderResponseDtos = orders.stream().map(order -> {
            return new OrderResponseDto(
                    order.getUserId().getId(),  // 주문자 ID
                    order.getAddress(),         // 주문 주소
                    order.getName(),            // 주문자 이름
                    order.getOrderTime(),        // 주문 시간
                    order.getStatus()           // 주문 상태
            );
        }).collect(Collectors.toList());

        return ResponseEntity.ok(orderResponseDtos);
    }
}
