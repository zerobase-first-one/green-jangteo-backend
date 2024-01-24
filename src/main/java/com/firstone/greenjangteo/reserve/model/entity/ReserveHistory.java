package com.firstone.greenjangteo.reserve.model.entity;

import com.firstone.greenjangteo.audit.BaseEntity;
import com.firstone.greenjangteo.reserve.dto.request.AddReserveRequestDto;
import com.firstone.greenjangteo.reserve.dto.request.UseReserveRequestDto;
import com.firstone.greenjangteo.reserve.model.CurrentReserve;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Entity(name = "reserve_history")
@Table(name = "reserve_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ReserveHistory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long orderId;
    private int addedReserve;
    private int usedReserve;

    @Convert(converter = CurrentReserve.CurrentReserveConverter.class)
    private CurrentReserve currentReserve;

    @Builder
    private ReserveHistory(Long userId, Long orderId, int addedReserve, int usedReserve, CurrentReserve currentReserve) {
        this.userId = userId;
        this.orderId = orderId;
        this.addedReserve = addedReserve;
        this.usedReserve = usedReserve;
        this.currentReserve = currentReserve;
    }

    public static ReserveHistory from(AddReserveRequestDto addReserveRequestDto, CurrentReserve currentReserve) {
        Long userId = Long.parseLong(addReserveRequestDto.getUserId());
        int addedReserve = addReserveRequestDto.getAddedReserve();

        return ReserveHistory.builder()
                .userId(userId)
                .addedReserve(addedReserve)
                .currentReserve(CurrentReserve.addReserve(currentReserve, addedReserve))
                .build();
    }

    public static ReserveHistory from
            (UseReserveRequestDto useReserveRequestDto, CurrentReserve currentReserve) {
        Long userId = Long.parseLong(useReserveRequestDto.getUserId());
        int usedReserve = useReserveRequestDto.getUsedReserve();

        return ReserveHistory.builder()
                .userId(userId)
                .usedReserve(usedReserve)
                .currentReserve(CurrentReserve.useReserve(currentReserve, usedReserve))
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReserveHistory that = (ReserveHistory) o;
        return addedReserve == that.addedReserve && usedReserve == that.usedReserve && Objects.equals(id, that.id) && Objects.equals(userId, that.userId) && Objects.equals(orderId, that.orderId) && Objects.equals(currentReserve, that.currentReserve);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, orderId, addedReserve, usedReserve, currentReserve);
    }
}
