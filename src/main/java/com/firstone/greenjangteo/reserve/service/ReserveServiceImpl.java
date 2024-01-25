package com.firstone.greenjangteo.reserve.service;

import com.firstone.greenjangteo.reserve.dto.request.AddReserveRequestDto;
import com.firstone.greenjangteo.reserve.dto.request.UseReserveRequestDto;
import com.firstone.greenjangteo.reserve.model.CurrentReserve;
import com.firstone.greenjangteo.reserve.model.entity.ReserveHistory;
import com.firstone.greenjangteo.reserve.repository.ReserveHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static com.firstone.greenjangteo.reserve.exception.message.NotFoundExceptionMessage.RESERVE_NOT_FOUND_EXCEPTION;

@Service
@RequiredArgsConstructor
public class ReserveServiceImpl implements ReserveService {
    private final ReserveHistoryRepository reserveHistoryRepository;

    public void addReserve(AddReserveRequestDto addReserveRequestDto) {
        Long userId = Long.parseLong(addReserveRequestDto.getUserId());
        ReserveHistory newReserveHistory;

        try {
            ReserveHistory reserveHistory = getCurrentReserve(userId);
            newReserveHistory
                    = ReserveHistory.from(addReserveRequestDto, reserveHistory.getCurrentReserve());
        } catch (EntityNotFoundException e) {
            newReserveHistory = ReserveHistory.from(addReserveRequestDto, new CurrentReserve(0));
        }

        reserveHistoryRepository.save(newReserveHistory);
    }

    @Override
    public void reduceReserve(UseReserveRequestDto useReserveRequestDto) {
        Long userId = Long.parseLong(useReserveRequestDto.getUserId());
        ReserveHistory reserveHistory = getCurrentReserve(userId);

        ReserveHistory newReserveHistory
                = ReserveHistory.from(useReserveRequestDto, reserveHistory.getCurrentReserve());

        reserveHistoryRepository.save(newReserveHistory);
    }

    @Override
    public List<ReserveHistory> getReserveHistories(Long userId) {
        return reserveHistoryRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public ReserveHistory getCurrentReserve(Long userId) {
        return reserveHistoryRepository.findFirstByUserIdOrderByCreatedAtDesc(userId)
                .orElseThrow(() -> new EntityNotFoundException(RESERVE_NOT_FOUND_EXCEPTION + userId));
    }
}