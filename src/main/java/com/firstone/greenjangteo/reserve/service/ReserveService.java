package com.firstone.greenjangteo.reserve.service;

import com.firstone.greenjangteo.reserve.dto.request.AddReserveRequestDto;
import com.firstone.greenjangteo.reserve.dto.request.UseReserveRequestDto;

public interface ReserveService {
    void addReserve(AddReserveRequestDto addReserveRequestDto);

    void reduceReserve(UseReserveRequestDto useReserveRequestDto);
}
