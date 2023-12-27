package com.firstone.greenjangteo.coupon.service;

import com.firstone.greenjangteo.coupon.model.entity.Coupon;
import com.firstone.greenjangteo.coupon.model.entity.CouponGroup;
import com.firstone.greenjangteo.coupon.repository.CouponGroupRepository;
import com.firstone.greenjangteo.coupon.repository.CouponRepository;
import com.firstone.greenjangteo.coupon.testutil.CouponTestObjectFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;

import static com.firstone.greenjangteo.coupon.excpeption.message.NotFoundExceptionMessage.COUPON_GROUP_ID_NOT_FOUND_EXCEPTION;
import static com.firstone.greenjangteo.coupon.excpeption.message.NotFoundExceptionMessage.COUPON_NAME_NOT_FOUND_EXCEPTION;
import static com.firstone.greenjangteo.coupon.testutil.CouponTestConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class CouponGroupServiceTest {
    @Autowired
    private CouponGroupService couponGroupService;

    @Autowired
    private CouponGroupRepository couponGroupRepository;

    @Autowired
    private CouponRepository couponRepository;

    private final LocalDate tomorrow = LocalDate.now().plusDays(1);

    @DisplayName("쿠폰 그룹 목록을 조회할 수 있다.")
    @Test
    void getCouponGroups() {
        // given
        CouponGroup couponGroup1
                = CouponTestObjectFactory.createCouponGroup(
                COUPON_NAME1, AMOUNT, DESCRIPTION, ISSUE_QUANTITY1, tomorrow, EXPIRATION_PERIOD1
        );
        CouponGroup couponGroup2
                = CouponTestObjectFactory.createCouponGroup(
                COUPON_NAME2, AMOUNT, DESCRIPTION, ISSUE_QUANTITY2, tomorrow, EXPIRATION_PERIOD2
        );
        couponGroupRepository.saveAll(List.of(couponGroup1, couponGroup2));

        // when
        List<CouponGroup> couponGroups = couponGroupService.getCouponGroups();

        // then
        assertThat(couponGroups).hasSize(2)
                .extracting("couponName")
                .containsExactlyInAnyOrder(COUPON_NAME1, COUPON_NAME2);
    }

    @DisplayName("쿠폰 그룹 ID를 통해 쿠폰 목록을 페이징 처리한 쿠폰 그룹을 찾을 수 있다.")
    @Test
    void getCouponGroupByCouponGroupId() {
        // given
        CouponGroup createdCouponGroup
                = CouponTestObjectFactory.createCouponGroup(
                COUPON_NAME1, AMOUNT, DESCRIPTION, ISSUE_QUANTITY1, tomorrow, EXPIRATION_PERIOD1
        );
        couponGroupRepository.save(createdCouponGroup);

        List<Coupon> createdCoupons = CouponTestObjectFactory.createCoupons(createdCouponGroup);
        couponRepository.saveAll(createdCoupons);

        // when
        List<Coupon> foundCoupons = couponGroupService.getCouponGroup(createdCouponGroup.getId(), Pageable.ofSize(2))
                .getContent();

        // then
        CouponGroup foundCouponGroup = foundCoupons.get(0).getCouponGroup();

        assertThat(foundCouponGroup.getCouponName()).isEqualTo(createdCouponGroup.getCouponName());
        assertThat(foundCouponGroup.getAmount()).isEqualTo(createdCouponGroup.getAmount());
        assertThat(foundCouponGroup.getDescription()).isEqualTo(createdCouponGroup.getDescription());
        assertThat(foundCouponGroup.getIssueQuantity()).isEqualTo(createdCouponGroup.getIssueQuantity());
        assertThat(foundCouponGroup.getScheduledIssueDate()).isEqualTo(createdCouponGroup.getScheduledIssueDate());
        assertThat(foundCouponGroup.getExpirationPeriod()).isEqualTo(createdCouponGroup.getExpirationPeriod());

        assertThat(foundCoupons).hasSize(2);
    }

    @DisplayName("전송된 쿠폰 그룹 ID를 가진 쿠폰 그룹이 존재하지 않으면 EntityNotFoundException이 발생한다.")
    @Test
    void getCouponGroupByNonExistentCouponGroupId() {
        // given
        Long couponGroupId = 1L;
        Pageable pageable = mock(Pageable.class);

        // when, then
        assertThatThrownBy(() -> couponGroupService.getCouponGroup(couponGroupId, pageable))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(COUPON_GROUP_ID_NOT_FOUND_EXCEPTION + couponGroupId);
    }

    @DisplayName("쿠폰 이름을 통해 쿠폰 그룹을 찾을 수 있다.")
    @Test
    void getCouponGroupByCouponName() {
        // given
        CouponGroup createdCouponGroup
                = CouponTestObjectFactory.createCouponGroup(
                COUPON_NAME1, AMOUNT, DESCRIPTION, ISSUE_QUANTITY1, tomorrow, EXPIRATION_PERIOD1
        );
        couponGroupRepository.save(createdCouponGroup);

        // when
        CouponGroup foundCouponGroup = couponGroupService.getCouponGroup(COUPON_NAME1);

        // then
        assertThat(foundCouponGroup.getCouponName()).isEqualTo(createdCouponGroup.getCouponName());
        assertThat(foundCouponGroup.getAmount()).isEqualTo(createdCouponGroup.getAmount());
        assertThat(foundCouponGroup.getDescription()).isEqualTo(createdCouponGroup.getDescription());
        assertThat(foundCouponGroup.getIssueQuantity()).isEqualTo(createdCouponGroup.getIssueQuantity());
        assertThat(foundCouponGroup.getScheduledIssueDate()).isEqualTo(createdCouponGroup.getScheduledIssueDate());
        assertThat(foundCouponGroup.getExpirationPeriod()).isEqualTo(createdCouponGroup.getExpirationPeriod());
    }

    @DisplayName("전송된 쿠폰 이름을 가진 쿠폰 그룹이 존재하지 않으면 EntityNotFoundException이 발생한다.")
    @Test
    void getCouponGroupByNonExistentCouponName() {
        // given, when, then
        assertThatThrownBy(() -> couponGroupService.getCouponGroup(COUPON_NAME1))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(COUPON_NAME_NOT_FOUND_EXCEPTION + COUPON_NAME1);
    }
}
