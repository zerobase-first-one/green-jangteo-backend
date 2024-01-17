package com.firstone.greenjangteo.reserve.model.entity;

import com.firstone.greenjangteo.reserve.dto.request.AddReserveRequestDto;
import com.firstone.greenjangteo.reserve.model.CurrentReserve;
import com.firstone.greenjangteo.reserve.testutil.ReserveTestObjectFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.firstone.greenjangteo.reserve.testutil.ReserveTestConstant.RESERVE1;
import static com.firstone.greenjangteo.reserve.testutil.ReserveTestConstant.RESERVE2;
import static com.firstone.greenjangteo.web.ApiConstant.ID_EXAMPLE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ReserveHistoryTest {
    @DisplayName("올바른 값을 전송하면 적립금 내역 인스턴스를 생성할 수 있다.")
    @Test
    void from() {
        // given
        AddReserveRequestDto addReserveRequestDto
                = ReserveTestObjectFactory.createAddReserveRequestDto(ID_EXAMPLE, RESERVE1);

        // when
        ReserveHistory reserveHistory = ReserveHistory.from(addReserveRequestDto, new CurrentReserve(RESERVE2));

        // then
        assertThat(reserveHistory.getUserId()).isEqualTo(Long.parseLong(ID_EXAMPLE));
        assertThat(reserveHistory.getAddedReserve()).isEqualTo(RESERVE1);
        assertThat(reserveHistory.getCurrentReserve()).isEqualTo(new CurrentReserve(RESERVE1 + RESERVE2));
    }

    @DisplayName("동일한 내부 값들을 전송하면 동등한 ReserveHistory 인스턴스를 생성한다.")
    @Test
    void fromSameValue() {
        // given, when
        AddReserveRequestDto addReserveRequestDto
                = ReserveTestObjectFactory.createAddReserveRequestDto(ID_EXAMPLE, RESERVE1);

        ReserveHistory reserveHistory1 = ReserveHistory.from(addReserveRequestDto, new CurrentReserve(RESERVE2));
        ReserveHistory reserveHistory2 = ReserveHistory.from(addReserveRequestDto, new CurrentReserve(RESERVE2));

        // then
        assertThat(reserveHistory1).isEqualTo(reserveHistory2);
        assertThat(reserveHistory1.hashCode()).isEqualTo(reserveHistory2.hashCode());
    }

    @DisplayName("다른 내부 값들을 전송하면 동등하지 않은 ReserveHistory 인스턴스를 생성한다.")
    @Test
    void fromDifferentValue() {
        // given, when
        AddReserveRequestDto addReserveRequestDto
                = ReserveTestObjectFactory.createAddReserveRequestDto(ID_EXAMPLE, RESERVE1);

        ReserveHistory reserveHistory1 = ReserveHistory.from(addReserveRequestDto, new CurrentReserve(RESERVE1));
        ReserveHistory reserveHistory2 = ReserveHistory.from(addReserveRequestDto, new CurrentReserve(RESERVE2));

        // then
        assertThat(reserveHistory1).isNotEqualTo(reserveHistory2);
        assertThat(reserveHistory1.hashCode()).isNotEqualTo(reserveHistory2.hashCode());
    }
}