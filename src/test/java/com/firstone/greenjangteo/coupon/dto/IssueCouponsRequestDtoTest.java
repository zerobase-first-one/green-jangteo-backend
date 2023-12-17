package com.firstone.greenjangteo.coupon.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import java.time.LocalDateTime;
import java.util.Set;

import static com.firstone.greenjangteo.coupon.testutil.CouponTestConstant.*;
import static org.assertj.core.api.Assertions.assertThat;

class IssueCouponsRequestDtoTest {
    @DisplayName("scheduledIssueDate 필드에 현재 이전의 시간을 입력하거나 시간을 입력하지 않으면 유효성 검사를 통과할 수 없다.")
    @Test
    void createByInvalidScheduledIssueDate() {
        // given
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        IssueCouponsRequestDto issueCouponsRequestDto1
                = IssueCouponsRequestDto.builder()
                .couponName(COUPON_NAME1)
                .amount(AMOUNT)
                .description(DESCRIPTION)
                .issueQuantity(ISSUE_QUANTITY)
                .scheduledIssueDate(LocalDateTime.now().plusDays(1))
                .expirationPeriod(EXPIRATION_PERIOD)
                .build();

        IssueCouponsRequestDto issueCouponsRequestDto2
                = IssueCouponsRequestDto.builder()
                .couponName(COUPON_NAME1)
                .amount(AMOUNT)
                .description(DESCRIPTION)
                .issueQuantity(ISSUE_QUANTITY)
                .scheduledIssueDate(LocalDateTime.now())
                .expirationPeriod(EXPIRATION_PERIOD)
                .build();

        IssueCouponsRequestDto issueCouponsRequestDto3
                = IssueCouponsRequestDto.builder()
                .couponName(COUPON_NAME1)
                .amount(AMOUNT)
                .description(DESCRIPTION)
                .issueQuantity(ISSUE_QUANTITY)
                .scheduledIssueDate(LocalDateTime.now().minusDays(1))
                .expirationPeriod(EXPIRATION_PERIOD)
                .build();

        IssueCouponsRequestDto issueCouponsRequestDto4
                = IssueCouponsRequestDto.builder()
                .couponName(COUPON_NAME1)
                .amount(AMOUNT)
                .description(DESCRIPTION)
                .issueQuantity(ISSUE_QUANTITY)
                .scheduledIssueDate(null)
                .expirationPeriod(EXPIRATION_PERIOD)
                .build();

        // when
        Set<ConstraintViolation<IssueCouponsRequestDto>> violations1 = validator.validate(issueCouponsRequestDto1);
        Set<ConstraintViolation<IssueCouponsRequestDto>> violations2 = validator.validate(issueCouponsRequestDto2);
        Set<ConstraintViolation<IssueCouponsRequestDto>> violations3 = validator.validate(issueCouponsRequestDto3);
        Set<ConstraintViolation<IssueCouponsRequestDto>> violations4 = validator.validate(issueCouponsRequestDto4);

        // then
        assertThat(violations1).isEmpty();
        assertThat(violations2).isNotEmpty();
        assertThat(violations3).isNotEmpty();
        assertThat(violations4).isNotEmpty();
    }
}