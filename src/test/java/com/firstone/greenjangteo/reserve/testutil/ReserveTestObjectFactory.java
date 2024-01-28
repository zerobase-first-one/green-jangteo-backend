package com.firstone.greenjangteo.reserve.testutil;

import com.firstone.greenjangteo.reserve.dto.request.AddReserveRequestDto;
import com.firstone.greenjangteo.reserve.model.CurrentReserve;
import com.firstone.greenjangteo.reserve.model.entity.ReserveHistory;

public class ReserveTestObjectFactory {
    public static AddReserveRequestDto createAddReserveRequestDto(String userId, int addedReserve) {
        return new AddReserveRequestDto(userId, addedReserve);
    }

    public static ReserveHistory createReserveHistory(Long userId, int addedReserve, CurrentReserve currentReserve) {
        return ReserveHistory.builder()
                .userId(userId)
                .addedReserve(addedReserve)
                .currentReserve(CurrentReserve.addReserve(currentReserve, addedReserve))
                .build();
    }
}
