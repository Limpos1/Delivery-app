package com.sparta.delivery.restaurant.dto;

import com.sparta.delivery.restaurant.enums.RestaurantCategory;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RestaurantRequestDto {

    private Long id; // 가게 수정 시 사용할 ID

    @NotBlank(message = "가게 이름은 필수입니다.")
    @Column(nullable = false, length = 50)
    private String name;

    @NotNull(message = "업종 선택은 필수입니다.")
    private RestaurantCategory category;

    @NotNull(message = "최소 주문 금액은 필수입니다.")
    @Min(value = 10000, message = "최소 주문 금액 10,000원 이상이어야 합니다.")
    private Long minOrderAmount;

    @NotNull(message = "오픈 시간은 필수입니다.")
    private String openTime;

    @NotNull(message = "마감 시간은 필수입니다.")
    private String  closeTime;

    // 가게 생성
    public RestaurantRequestDto(String name, RestaurantCategory category, Long minOrderAmount, String openTime, String closeTime) {
        this.name = name;
        this.category = category;
        this.minOrderAmount = minOrderAmount;
        this.openTime = openTime;
        this.closeTime = closeTime;
    }

    // 가게 수정
    public RestaurantRequestDto(Long id, String name, RestaurantCategory category, Long minOrderAmount, String openTime, String closeTime) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.minOrderAmount = minOrderAmount;
        this.openTime = openTime;
        this.closeTime = closeTime;
    }
}
