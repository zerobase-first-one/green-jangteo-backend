package com.firstone.greenjangteo.coupon.service;

import com.firstone.greenjangteo.coupon.dto.IssueCouponsRequestDto;
import com.firstone.greenjangteo.coupon.model.Amount;
import com.firstone.greenjangteo.coupon.model.ExpirationPeriod;
import com.firstone.greenjangteo.coupon.model.IssueQuantity;
import com.firstone.greenjangteo.coupon.model.entity.Coupon;
import com.firstone.greenjangteo.coupon.model.entity.CouponGroup;
import com.firstone.greenjangteo.coupon.repository.CouponGroupRepository;
import com.firstone.greenjangteo.coupon.repository.CouponRepository;
import com.firstone.greenjangteo.coupon.testutil.CouponTestObjectFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static com.firstone.greenjangteo.coupon.testutil.CouponTestConstant.*;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class CouponServiceTest {
    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponGroupRepository couponGroupRepository;

    @Autowired
    private CouponRepository couponRepository;

    @DisplayName("올바른 쿠폰 발행 양식을 전송하면 대량의 쿠폰을 등록할 수 있다.")
    @Test
    void createCoupons() throws JobExecutionException {
        // given
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        IssueCouponsRequestDto issueCouponsRequestDto
                = CouponTestObjectFactory.createIssueCouponsRequestDto(
                COUPON_NAME1, AMOUNT, DESCRIPTION, ISSUE_QUANTITY, tomorrow, EXPIRATION_PERIOD
        );

        // when
        couponService.createCoupons(issueCouponsRequestDto);
        CouponGroup couponGroup = couponGroupRepository.findByCouponName(COUPON_NAME1).get();
        List<Coupon> coupons = couponRepository.findAll();

        // then
        assertThat(couponGroup.getCouponName()).isEqualTo(COUPON_NAME1);
        assertThat(couponGroup.getAmount()).isEqualTo(Amount.of(AMOUNT));
        assertThat(couponGroup.getDescription()).isEqualTo(DESCRIPTION);
        assertThat(couponGroup.getIssueQuantity()).isEqualTo(IssueQuantity.of(ISSUE_QUANTITY));
        assertThat(couponGroup.getScheduledIssueDate()).isEqualTo(tomorrow);
        assertThat(couponGroup.getExpirationPeriod()).isEqualTo(ExpirationPeriod.of(EXPIRATION_PERIOD));
        assertThat(coupons).hasSize(Integer.parseInt(ISSUE_QUANTITY));
    }
}