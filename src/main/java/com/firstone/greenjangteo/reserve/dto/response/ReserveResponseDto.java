package com.firstone.greenjangteo.reserve.dto.response;

import com.firstone.greenjangteo.reserve.model.entity.ReserveHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Getter
public class ReserveResponseDto {
    private Long orderId;
    private int addedReserve;
    private int usedReserve;
    private int currentReserve;
    private LocalDateTime createdAt;

    public static ReserveResponseDto from(ReserveHistory reserveHistory) {
        return ReserveResponseDto.builder()
                .orderId(reserveHistory.getOrderId())
                .addedReserve(reserveHistory.getAddedReserve())
                .usedReserve(reserveHistory.getUsedReserve())
                .currentReserve(reserveHistory.getCurrentReserve().getValue())
                .createdAt(reserveHistory.getCreatedAt())
                .build();
    }
}
