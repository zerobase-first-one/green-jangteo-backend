package com.firstone.greenjangteo.coupon.model.entity;

import com.firstone.greenjangteo.application.model.CouponGroupModel;
import com.firstone.greenjangteo.coupon.model.Amount;
import com.firstone.greenjangteo.coupon.model.ExpirationPeriod;
import com.firstone.greenjangteo.coupon.model.IssueQuantity;
import com.firstone.greenjangteo.coupon.testutil.CouponTestObjectFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static com.firstone.greenjangteo.coupon.testutil.CouponTestConstant.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class CouponGroupTest {
    @DisplayName("올바른 값을 전송하면 쿠폰 그룹 인스턴스를 생성할 수 있다.")
    @Test
    void from() {
        // given
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1)
                .truncatedTo(ChronoUnit.MILLIS);

        CouponGroupModel couponGroupModel
                = CouponTestObjectFactory.createCouponGroupModel(
                COUPON_NAME1, AMOUNT, DESCRIPTION, ISSUE_QUANTITY, tomorrow, EXPIRATION_PERIOD
        );

        // when
        CouponGroup couponGroup = CouponGroup.from(couponGroupModel);

        // then
        Assertions.assertThat(couponGroup.getCouponName()).isEqualTo(COUPON_NAME1);
        Assertions.assertThat(couponGroup.getAmount()).isEqualTo(Amount.of(AMOUNT));
        Assertions.assertThat(couponGroup.getDescription()).isEqualTo(DESCRIPTION);
        Assertions.assertThat(couponGroup.getIssueQuantity()).isEqualTo(IssueQuantity.of(ISSUE_QUANTITY));
        Assertions.assertThat(couponGroup.getScheduledIssueDate()).isEqualTo(tomorrow);
        Assertions.assertThat(couponGroup.getExpirationPeriod()).isEqualTo(ExpirationPeriod.of(EXPIRATION_PERIOD));
    }

    @DisplayName("동일한 내부 값들을 전송하면 동등한 CouponGroup 인스턴스를 생성한다.")
    @Test
    void fromSameValue() {
        // given
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1)
                .truncatedTo(ChronoUnit.MILLIS);

        CouponGroupModel couponGroupModel
                = CouponTestObjectFactory.createCouponGroupModel(
                COUPON_NAME1, AMOUNT, DESCRIPTION, ISSUE_QUANTITY, tomorrow, EXPIRATION_PERIOD
        );

        // when
        CouponGroup couponGroup1 = CouponGroup.from(couponGroupModel);
        CouponGroup couponGroup2 = CouponGroup.from(couponGroupModel);

        // then
        assertThat(couponGroup1).isEqualTo(couponGroup2);
        assertThat(couponGroup1.hashCode()).isEqualTo(couponGroup2.hashCode());
    }

    @DisplayName("다른 내부 값들을 전송하면 동등하지 않은 CouponGroup 인스턴스를 생성한다.")
    @Test
    void fromDifferentValue() {
        // given
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1)
                .truncatedTo(ChronoUnit.MILLIS);

        LocalDateTime dayAfterTomorrow = LocalDateTime.now().plusDays(2)
                .truncatedTo(ChronoUnit.MILLIS);

        CouponGroupModel couponGroupModel1
                = CouponTestObjectFactory.createCouponGroupModel(
                COUPON_NAME1, AMOUNT, DESCRIPTION, ISSUE_QUANTITY, tomorrow, EXPIRATION_PERIOD
        );

        CouponGroupModel couponGroupModel2
                = CouponTestObjectFactory.createCouponGroupModel(
                COUPON_NAME1, AMOUNT, DESCRIPTION, ISSUE_QUANTITY, dayAfterTomorrow, EXPIRATION_PERIOD
        );

        // when
        CouponGroup couponGroup1 = CouponGroup.from(couponGroupModel1);
        CouponGroup couponGroup2 = CouponGroup.from(couponGroupModel2);

        // then
        assertThat(couponGroup1).isNotEqualTo(couponGroup2);
        assertThat(couponGroup1.hashCode()).isNotEqualTo(couponGroup2.hashCode());
    }
}