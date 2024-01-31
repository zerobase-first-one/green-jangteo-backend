package com.firstone.greenjangteo.reserve.model;

import com.firstone.greenjangteo.reserve.exception.serious.InsufficientReserveException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static com.firstone.greenjangteo.reserve.exception.message.InsufficientExceptionMessage.*;
import static com.firstone.greenjangteo.reserve.exception.message.InvalidExceptionMessage.INVALID_UPDATING_RESERVE_EXCEPTION;
import static com.firstone.greenjangteo.reserve.testutil.ReserveTestConstant.RESERVE1;
import static com.firstone.greenjangteo.reserve.testutil.ReserveTestConstant.RESERVE2;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ActiveProfiles("test")
class CurrentReserveTest {
    @DisplayName("동일한 현재 적립금을 전송하면 동등한 CurrentReserve 인스턴스를 생성한다.")
    @Test
    void ofSameValue() {
        // given, when
        CurrentReserve currentReserve1 = new CurrentReserve(RESERVE1);
        CurrentReserve currentReserve2 = new CurrentReserve(RESERVE1);

        // then
        assertThat(currentReserve1).isEqualTo(currentReserve2);
        assertThat(currentReserve1.hashCode()).isEqualTo(currentReserve2.hashCode());
    }

    @DisplayName("다른 현재 적립금을 전송하면 동등하지 않은 CurrentReserve 인스턴스를 생성한다.")
    @Test
    void ofDifferentValue() {
        // given, when
        CurrentReserve currentReserve1 = new CurrentReserve(RESERVE1);
        CurrentReserve currentReserve2 = new CurrentReserve(RESERVE2);

        // then
        assertThat(currentReserve1).isNotEqualTo(currentReserve2);
        assertThat(currentReserve1.hashCode()).isNotEqualTo(currentReserve2.hashCode());
    }

    @DisplayName("현재 적립금과 추가할 적립금을 전송해 적립금을 추가할 수 있다.")
    @Test
    void addReserve() {
        // given
        CurrentReserve currentReserve = new CurrentReserve(RESERVE1);

        // when
        CurrentReserve addedCurrentReserve = CurrentReserve.addReserve(currentReserve, RESERVE2);

        // then
        assertThat(addedCurrentReserve.getValue()).isEqualTo(RESERVE1 + RESERVE2);
    }

    @DisplayName("전송된 추가할 적립금이 음수이면 IllegalArgumentException이 발생한다.")
    @Test
    void addReserveFromMinusValue() {
        // given
        CurrentReserve currentReserve = new CurrentReserve(RESERVE1);
        int addedReserve = -RESERVE2;

        // when, then
        assertThatThrownBy(() -> CurrentReserve.addReserve(currentReserve, addedReserve))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(INVALID_UPDATING_RESERVE_EXCEPTION + addedReserve);
    }

    @DisplayName("현재 적립금과 차감할 적립금을 전송해 적립금을 차감할 수 있다.")
    @Test
    void useReserve() {
        // given
        CurrentReserve currentReserve = new CurrentReserve(RESERVE2);

        // when
        CurrentReserve reducedCurrentReserve = CurrentReserve.useReserve(currentReserve, RESERVE1);

        // then
        assertThat(reducedCurrentReserve.getValue()).isEqualTo(RESERVE2 - RESERVE1);
    }

    @DisplayName("전송된 현재 적립금이 음수이면 InsufficientReserveException이 발생한다.")
    @Test
    void useReserveFromMinusCurrentReserve() {
        // given
        int currentReserveValue = -RESERVE2;
        CurrentReserve currentReserve = new CurrentReserve(currentReserveValue);

        // when, then
        assertThatThrownBy(() -> CurrentReserve.useReserve(currentReserve, RESERVE1))
                .isInstanceOf(InsufficientReserveException.class)
                .hasMessage(INSUFFICIENT_CURRENT_RESERVE_EXCEPTION + currentReserveValue);
    }

    @DisplayName("전송된 차감할 적립금이 음수이면 IllegalArgumentException이 발생한다.")
    @Test
    void useReserveFromMinusValue() {
        // given
        CurrentReserve currentReserve = new CurrentReserve(RESERVE1);
        int usedReserve = -RESERVE2;

        // when, then
        assertThatThrownBy(() -> CurrentReserve.useReserve(currentReserve, usedReserve))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(INVALID_UPDATING_RESERVE_EXCEPTION + usedReserve);
    }

    @DisplayName("차감할 적립금이 부족하면 InsufficientReserveException이 발생한다.")
    @Test
    void useReserveFromInsufficientCurrentReserve() {
        // given
        CurrentReserve currentReserve = new CurrentReserve(RESERVE1);

        // when, then
        assertThatThrownBy(() -> CurrentReserve.useReserve(currentReserve, RESERVE2))
                .isInstanceOf(InsufficientReserveException.class)
                .hasMessage(INSUFFICIENT_NEW_RESERVE_EXCEPTION1 + RESERVE1
                        + INSUFFICIENT_NEW_RESERVE_EXCEPTION2 + RESERVE2);
    }
}