package com.sparta.delivery.user.entity;

import com.sparta.delivery.cart.entity.Cart;
import com.sparta.delivery.etc.common.Timestamped;
import com.sparta.delivery.orders.entity.Orders;
import com.sparta.delivery.restaurant.entity.Restaurant;
import com.sparta.delivery.review.entity.Review;
import com.sparta.delivery.user.dto.SignupRequestDto;
import com.sparta.delivery.user.enums.UserRole;
import com.sparta.delivery.user.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Setter
@Entity
@Getter
@NoArgsConstructor
//SQL identifier 예약어관련 오류로 테이블 이름만 재지정
@Table(name = "users")
public class User extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    @Email
    private String email; // 사용자 아이디 (이메일)

    @Column(nullable = false, length = 100)
    private String password; // 비밀번호

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role; //USER, OWNER

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @OneToOne(mappedBy = "userId")
    private Cart cart;

    @OneToMany(mappedBy = "userId")
    private List<Orders> orderList = new ArrayList<>();

    @OneToMany(mappedBy = "userId")
    private List<Review> reviewList = new ArrayList<>();

    @OneToMany(mappedBy = "ownerId")
    private List<Restaurant> restaurantList = new ArrayList<>();

    public User(SignupRequestDto requestDto, String encodedPassword) {
        this.email = requestDto.getEmail();
        this.password = encodedPassword;
        this.role = requestDto.getRole();
        this.status = UserStatus.NON_WITHDRAWAL;
    }

    public void update() {
        this.status = UserStatus.WITHDRAWAL;
    }
}
