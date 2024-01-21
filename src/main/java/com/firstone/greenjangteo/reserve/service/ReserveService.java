package com.firstone.greenjangteo.reserve.service;

import com.firstone.greenjangteo.reserve.dto.request.AddReserveRequestDto;
import com.firstone.greenjangteo.reserve.dto.request.UseReserveRequestDto;
import com.firstone.greenjangteo.reserve.model.entity.ReserveHistory;

import java.util.List;

public interface ReserveService {
    void addReserve(AddReserveRequestDto addReserveRequestDto);

    void reduceReserve(UseReserveRequestDto useReserveRequestDto);


    List<ReserveHistory> getReserveHistories(Long userId);
}
