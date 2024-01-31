package com.firstone.greenjangteo.reserve.service;

import com.firstone.greenjangteo.reserve.dto.request.AddReserveRequestDto;
import com.firstone.greenjangteo.reserve.dto.request.UseReserveRequestDto;
import com.firstone.greenjangteo.reserve.model.entity.ReserveHistory;

import java.util.List;

public interface ReserveService {
    void addReserve(AddReserveRequestDto addReserveRequestDto);

    void reduceReserve(UseReserveRequestDto useReserveRequestDto);

    void useReserve(Long orderId, UseReserveRequestDto useReserveRequestDto);

    void rollBackUsedReserve(Long orderId, AddReserveRequestDto addReserveRequestDto);

    List<ReserveHistory> getReserveHistories(Long userId);

    ReserveHistory getCurrentReserve(Long userId);
}
