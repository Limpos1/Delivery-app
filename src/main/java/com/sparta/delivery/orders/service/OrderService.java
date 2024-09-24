package com.sparta.delivery.orders.service;

import com.sparta.delivery.cart.entity.Cart;
import com.sparta.delivery.cart.entity.CartItem;
import com.sparta.delivery.orders.dto.OrderDetailDto;
import com.sparta.delivery.orders.dto.OrderRequestDto;
import com.sparta.delivery.orders.dto.OrderResponseDto;
import com.sparta.delivery.orders.dto.CombineDto;
import com.sparta.delivery.orders.repository.OrderDetailRepository;
import com.sparta.delivery.orders.repository.OrdersRepository;
import com.sparta.delivery.orders.entity.OrderDetail;
import com.sparta.delivery.orders.entity.Orders;
import com.sparta.delivery.orders.enums.OrderStatus;
import com.sparta.delivery.restaurant.entity.Restaurant;
import com.sparta.delivery.restaurant.repository.RestaurantRepository;
import com.sparta.delivery.user.entity.User;
import com.sparta.delivery.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import static com.sparta.delivery.orders.enums.OrderStatus.PENDING;
import static com.sparta.delivery.user.enums.UserRole.OWNER;

@Service
public class OrderService {


    private final OrdersRepository ordersRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    public OrderService(OrdersRepository ordersRepository, OrderDetailRepository orderDetailRepository, UserRepository userRepository, RestaurantRepository restaurantRepository) {
        this.ordersRepository = ordersRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
    }
    public ResponseEntity<CombineDto> requestOrder(Long userId, OrderRequestDto req,String inputName) {
        LocalDateTime ordertime = LocalDateTime.now();
        LocalDateTime openTime;
        LocalDateTime closeTime;
        Long totalPrice = 0L;
        List<OrderDetailDto> orderDetails = new ArrayList<>();

        User user = userRepository.findById(userId).orElseThrow(()-> new IllegalArgumentException("User not found"));
        Restaurant rest = restaurantRepository.findById(req.getRestaurantId()).orElseThrow(()->new IllegalArgumentException("등록된 식당이 없습니다."));
        Cart cart = user.getCart();

        //---------------------------------------------------------------------------------------------------

        int openHour = rest.getOpenTime().getHour();
        int openMinute = rest.getOpenTime().getMinute();
        int closeHour = rest.getCloseTime().getHour();
        int closeMinute = rest.getCloseTime().getMinute();

        openTime = LocalDateTime.of(ordertime.getYear(),ordertime.getMonthValue(),ordertime.getDayOfMonth(),openHour,openMinute);
        //만약 오후에 열고 새벽에 닫는 가게라면 현재 날짜에서 +1을 하여 다음날까지 계산
        if(closeHour < openHour){
            LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
            closeTime = LocalDateTime.of(tomorrow.getYear(), tomorrow.getMonth(), tomorrow.getDayOfMonth(), closeHour, closeMinute);
        }else{
            closeTime = LocalDateTime.of(ordertime.getYear(), ordertime.getMonth(), ordertime.getDayOfMonth(), closeHour, closeMinute);
        }
        //-----------------------------------------------------------------------------------------------------

        if(ordertime.isBefore(openTime) || ordertime.isAfter(closeTime)){
            throw new IllegalArgumentException("영업 시간이 아닙니다.");
        }

        Long price = req.getPrice();
        if(price<rest.getMinOrderAmount()){
            throw new IllegalArgumentException("최소 주문 금액 이상이어야 합니다.");
        }
        String name = inputName;
        String address = req.getAddress();
        OrderStatus orderstatus = PENDING;
        Long restaurantid = rest.getId();
        Long count = (long)cart.getCartItems().size();
        Orders orders = new Orders(user, address, name,rest,ordertime, orderstatus,count,totalPrice);
        Long ordersId=orders.getId();

        for(CartItem cartItem : cart.getCartItems()){
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrdersId(orders);
            orderDetail.setRestaurantId(restaurantid);
            orderDetail.setMenuId(cartItem.getMenu().getId());
            orderDetail.setMenuName(cartItem.getMenu().getName());
            orderDetail.setPrice(cartItem.getMenu().getPrice());

            totalPrice+=cartItem.getMenu().getPrice();

            orderDetail.setOrderTime(ordertime);
            orderDetail.setRestaurantId(rest.getId());
            orderDetailRepository.save(orderDetail);
            OrderDetailDto dtailDto = new OrderDetailDto(ordersId,orderDetail.getMenuId(),orderDetail.getMenuName(),orderDetail.getRestaurantId(), (long) orderDetail.getPrice(),ordertime);
            orderDetails.add(dtailDto);
        }


        orders.setTotalPrice(totalPrice);
        ordersRepository.save(orders);


        Long orderid = orders.getId();

        //orders Dto
        OrderResponseDto orderDto = new OrderResponseDto(userId,address,name,ordertime,orderstatus,orderid,count, totalPrice,restaurantid);
        CombineDto resDto = new CombineDto(orderDto, orderDetails);

        return ResponseEntity.ok(resDto);

    }

    public ResponseEntity<CombineDto> getOrder(long id) {

        List<OrderDetail> orderDetails;
        List<OrderDetailDto> orderDetailDtos = new ArrayList<>();
        //주문을 찾아서 저장 없다면 예외처리
        Orders order = ordersRepository.findById(id).orElse(null);
        if(order==null){
            throw new IllegalArgumentException("Order not found");
        }

        orderDetails = orderDetailRepository.findAllByOrdersId(order);

        //주문 정보를 Dto에 담기위한 과정
        Long userId = order.getUserId().getId();
        String address = order.getAddress();
        String name = order.getName();
        LocalDateTime orderTime = order.getOrderTime();
        OrderStatus status = order.getStatus();
        Long orderId = order.getId();
        Long count = order.getCount();
        Long totalPrice = order.getTotalPrice();
        Long restaurantid = order.getRestaurant().getId();

        OrderResponseDto orderDto = new OrderResponseDto(userId, address, name, orderTime, status, orderId, count, totalPrice,restaurantid);

        //상세 정보를 Dto에 담기위한 과정
        for(OrderDetail orderDetail : orderDetails){
            Long orderid = orderDetail.getOrdersId().getId();
            Long menuid = orderDetail.getMenuId();
            Long restaurantId = orderDetail.getRestaurantId();
            String menuName = orderDetail.getMenuName();
            Long price = (long) orderDetail.getPrice();
            LocalDateTime ordertime = orderDetail.getOrderTime();
            OrderDetailDto detailDto = new OrderDetailDto(orderid, menuid,menuName, restaurantId, price, ordertime);
            orderDetailDtos.add(detailDto);
        }





        CombineDto resDto = new CombineDto(orderDto,orderDetailDtos);


        return ResponseEntity.ok(resDto);
    }

    public OrderStatus updateOrder(Long userId, Long orderid, OrderStatus oEnum) {
        Orders order = ordersRepository.findById(orderid).orElse(null);
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
            ordersRepository.save(order);
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
        List<Orders> orders = ordersRepository.findAllByRestaurantId(restaurantId);

        // 각 주문을 OrderResponseDto로 변환하여 리스트 생성
        List<OrderResponseDto> orderResponseDtos = orders.stream().map(order -> {
            return new OrderResponseDto(
                    order.getUserId().getId(),  // 주문자 ID
                    order.getAddress(),         // 주문 주소
                    order.getName(),// 주문자 이름
                    order.getOrderTime(),
                    order.getStatus(),
                    order.getId(),
                    order.getCount(),
                    order.getTotalPrice(),
                    order.getRestaurant().getId()
            );
        }).collect(Collectors.toList());

        return ResponseEntity.ok(orderResponseDtos);
    }
}
