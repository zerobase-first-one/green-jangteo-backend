package com.firstone.greenjangteo.coupon.model.entity;

import com.firstone.greenjangteo.application.model.CouponGroupModel;
import com.firstone.greenjangteo.coupon.exception.serious.InconsistentCouponSizeException;
import com.firstone.greenjangteo.coupon.exception.serious.InsufficientRemainingQuantityException;
import com.firstone.greenjangteo.coupon.model.Amount;
import com.firstone.greenjangteo.coupon.model.ExpirationPeriod;
import com.firstone.greenjangteo.coupon.model.IssueQuantity;
import com.firstone.greenjangteo.coupon.repository.CouponGroupRepository;
import com.firstone.greenjangteo.coupon.repository.CouponRepository;
import com.firstone.greenjangteo.coupon.testutil.CouponTestObjectFactory;
import com.firstone.greenjangteo.user.model.entity.User;
import com.firstone.greenjangteo.user.repository.UserRepository;
import com.firstone.greenjangteo.user.testutil.UserTestObjectFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

import static com.firstone.greenjangteo.coupon.exception.message.AbnormalStateExceptionMessage.*;
import static com.firstone.greenjangteo.coupon.testutil.CouponTestConstant.*;
import static com.firstone.greenjangteo.user.model.Role.ROLE_BUYER;
import static com.firstone.greenjangteo.user.testutil.UserTestConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class CouponGroupTest {
    @Autowired
    private CouponGroupRepository couponGroupRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EntityManager entityManager;

    LocalDate tomorrow = LocalDate.now().plusDays(1);

    @DisplayName("올바른 값을 전송하면 쿠폰 그룹 인스턴스를 생성할 수 있다.")
    @Test
    void from() {
        // given
        CouponGroupModel couponGroupModel
                = CouponTestObjectFactory.createCouponGroupModel(
                COUPON_NAME1, AMOUNT, DESCRIPTION, ISSUE_QUANTITY1, tomorrow, EXPIRATION_PERIOD1
        );

        // when
        CouponGroup couponGroup = CouponGroup.from(couponGroupModel);

        // then
        Assertions.assertThat(couponGroup.getCouponName()).isEqualTo(COUPON_NAME1);
        Assertions.assertThat(couponGroup.getAmount()).isEqualTo(Amount.of(AMOUNT));
        Assertions.assertThat(couponGroup.getDescription()).isEqualTo(DESCRIPTION);
        Assertions.assertThat(couponGroup.getIssueQuantity()).isEqualTo(IssueQuantity.of(ISSUE_QUANTITY1));
        Assertions.assertThat(couponGroup.getScheduledIssueDate()).isEqualTo(tomorrow);
        Assertions.assertThat(couponGroup.getExpirationPeriod()).isEqualTo(ExpirationPeriod.of(EXPIRATION_PERIOD1));
    }

    @DisplayName("동일한 내부 값들을 전송하면 동등한 CouponGroup 인스턴스를 생성한다.")
    @Test
    void fromSameValue() {
        // given
        CouponGroupModel couponGroupModel
                = CouponTestObjectFactory.createCouponGroupModel(
                COUPON_NAME1, AMOUNT, DESCRIPTION, ISSUE_QUANTITY1, tomorrow, EXPIRATION_PERIOD1
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
        LocalDate dayAfterTomorrow = LocalDate.now().plusDays(2);

        CouponGroupModel couponGroupModel1
                = CouponTestObjectFactory.createCouponGroupModel(
                COUPON_NAME1, AMOUNT, DESCRIPTION, ISSUE_QUANTITY1, tomorrow, EXPIRATION_PERIOD1
        );

        CouponGroupModel couponGroupModel2
                = CouponTestObjectFactory.createCouponGroupModel(
                COUPON_NAME1, AMOUNT, DESCRIPTION, ISSUE_QUANTITY1, dayAfterTomorrow, EXPIRATION_PERIOD1
        );

        // when
        CouponGroup couponGroup1 = CouponGroup.from(couponGroupModel1);
        CouponGroup couponGroup2 = CouponGroup.from(couponGroupModel2);

        // then
        assertThat(couponGroup1).isNotEqualTo(couponGroup2);
        assertThat(couponGroup1.hashCode()).isNotEqualTo(couponGroup2.hashCode());
    }

    @DisplayName("필요한 쿠폰 수량과 남은 쿠폰 수량의 차이만큼 쿠폰을 생성하고, 발행 매수와 남은 쿠폰 수량을 그만큼 증가시킨다.")
    @Test
    void addCoupons() {
        // given
        CouponGroup couponGroup = CouponTestObjectFactory.createCouponGroup(
                COUPON_NAME1, AMOUNT, DESCRIPTION, ISSUE_QUANTITY4, tomorrow, EXPIRATION_PERIOD1
        );

        List<Coupon> coupons = CouponTestObjectFactory.createCoupons(couponGroup);
        couponGroupRepository.save(couponGroup);
        couponRepository.saveAll(coupons);

        entityManager.refresh(couponGroup);

        int requiredQuantity = Integer.parseInt(QUANTITY_TO_PROVIDE);

        // when
        couponGroup.addInsufficientCoupons(requiredQuantity);

        // then
        assertThat(couponGroup.getCoupons()).hasSize(requiredQuantity);
        assertThat(couponGroup.getIssueQuantity()).isEqualTo(IssueQuantity.of(String.valueOf(requiredQuantity)));
        assertThat(couponGroup.getRemainingQuantity()).isEqualTo(requiredQuantity);
    }

    @DisplayName("쿠폰들에 회원을 추가하고, 남은 쿠폰 수량을 감소시킨다.")
    @Test
    void issueAndAddUserToCoupons() {
        // given
        User user = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.name())
        );
        CouponGroup couponGroup = CouponTestObjectFactory.createCouponGroup(
                COUPON_NAME1, AMOUNT, DESCRIPTION, QUANTITY_TO_PROVIDE, tomorrow, EXPIRATION_PERIOD1
        );
        List<Coupon> coupons = CouponTestObjectFactory.createCoupons(couponGroup);

        userRepository.save(user);
        couponGroupRepository.save(couponGroup);
        couponRepository.saveAll(coupons);

        int requiredQuantity = Integer.parseInt(QUANTITY_TO_PROVIDE);
        int remainingQuantity = couponGroup.getRemainingQuantity();

        // when
        couponGroup.issueAndAddUserToCoupons(user, coupons, requiredQuantity);

        // then
        assertThat(coupons).hasSize(requiredQuantity)
                .extracting("user")
                .containsOnly(user);
        assertThat(couponGroup.getRemainingQuantity()).isEqualTo(remainingQuantity - requiredQuantity);
    }

    @DisplayName("전송된 쿠폰들의 수량이 필요로 하는 발행 매수와 일치하지 않으면 InconsistentCouponSizeException이 발생한다.")
    @Test
    void issueAndAddUserToCouponsWithInconsistentCouponSize() {
        // given
        User user = mock(User.class);
        CouponGroup couponGroup = CouponTestObjectFactory.createCouponGroup(
                COUPON_NAME1, AMOUNT, DESCRIPTION, ISSUE_QUANTITY3, tomorrow, EXPIRATION_PERIOD1
        );
        List<Coupon> coupons = CouponTestObjectFactory.createCoupons(couponGroup);

        int requiredQuantity = Integer.parseInt(QUANTITY_TO_PROVIDE);

        // when, then
        assertThatThrownBy(() -> couponGroup.issueAndAddUserToCoupons(user, coupons, requiredQuantity))
                .isInstanceOf(InconsistentCouponSizeException.class)
                .hasMessage(INCONSISTENT_COUPON_SIZE_EXCEPTION + coupons.size()
                        + INCONSISTENT_COUPON_SIZE_EXCEPTION_REQUIRED_QUANTITY + requiredQuantity);
    }

    @DisplayName("남은 쿠폰 수량이 지급하려는 쿠폰들의 수량보다 부족하면 InsufficientRemainingQuantityException이 발생한다.")
    @Test
    void issueAndAddUserToCouponsWithInsufficientRemainingQuantity() {
        // given
        User user = mock(User.class);
        CouponGroup couponGroup = CouponTestObjectFactory.createCouponGroup(
                COUPON_NAME1, AMOUNT, DESCRIPTION, QUANTITY_TO_PROVIDE, tomorrow, EXPIRATION_PERIOD1
        );
        List<Coupon> coupons = CouponTestObjectFactory.createCoupons(couponGroup);

        int requiredQuantity = Integer.parseInt(QUANTITY_TO_PROVIDE);
        couponGroup.issueAndAddUserToCoupons(user, coupons, requiredQuantity);

        // when, then
        assertThatThrownBy(() -> couponGroup.issueAndAddUserToCoupons(user, coupons, requiredQuantity))
                .isInstanceOf(InsufficientRemainingQuantityException.class)
                .hasMessage(INSUFFICIENT_REMAINING_QUANTITY_EXCEPTION + couponGroup.getRemainingQuantity()
                        + INSUFFICIENT_REMAINING_QUANTITY_EXCEPTION_QUANTITY_TO_PROVIDE + coupons.size());
    }

    @DisplayName("회원에게 지급되지 않은 쿠폰들을 반환한다.")
    @Test
    void getUnassignedCoupons() {
        // given
        CouponGroup couponGroup = CouponTestObjectFactory.createCouponGroup(
                COUPON_NAME1, AMOUNT, DESCRIPTION, ISSUE_QUANTITY3, tomorrow, EXPIRATION_PERIOD1
        );
        couponGroupRepository.save(couponGroup);

        List<Coupon> coupons = CouponTestObjectFactory.createCoupons(couponGroup);

        User user1 = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.name())
        );
        User user2 = UserTestObjectFactory.createUser(
                EMAIL2, USERNAME2, PASSWORD2, passwordEncoder, FULL_NAME2, PHONE2, List.of(ROLE_BUYER.name())
        );
        userRepository.saveAll(List.of(user1, user2));

        coupons.get(0).addUser(user1);
        coupons.get(1).addUser(user2);
        couponRepository.saveAll(coupons);

        entityManager.refresh(couponGroup);

        // when
        List<Coupon> unassignedCoupons = couponGroup.getUnassignedCoupons();

        // then
        assertThat(unassignedCoupons).hasSize(Integer.parseInt(ISSUE_QUANTITY3) - 2);
    }
}
