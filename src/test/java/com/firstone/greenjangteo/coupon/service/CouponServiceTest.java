package com.firstone.greenjangteo.coupon.service;

import com.firstone.greenjangteo.coupon.dto.request.IssueCouponsRequestDto;
import com.firstone.greenjangteo.coupon.dto.request.ProvideCouponsToUserRequestDto;
import com.firstone.greenjangteo.coupon.dto.request.ProvideCouponsToUsersRequestDto;
import com.firstone.greenjangteo.coupon.exception.serious.AlreadyUsedCouponException;
import com.firstone.greenjangteo.coupon.model.Amount;
import com.firstone.greenjangteo.coupon.model.ExpirationPeriod;
import com.firstone.greenjangteo.coupon.model.IssueQuantity;
import com.firstone.greenjangteo.coupon.model.entity.Coupon;
import com.firstone.greenjangteo.coupon.model.entity.CouponGroup;
import com.firstone.greenjangteo.coupon.repository.CouponGroupRepository;
import com.firstone.greenjangteo.coupon.repository.CouponRepository;
import com.firstone.greenjangteo.coupon.testutil.CouponTestObjectFactory;
import com.firstone.greenjangteo.user.model.entity.User;
import com.firstone.greenjangteo.user.repository.UserRepository;
import com.firstone.greenjangteo.user.service.AuthenticationService;
import com.firstone.greenjangteo.user.testutil.UserTestObjectFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.firstone.greenjangteo.coupon.exception.message.AbnormalStateExceptionMessage.ALREADY_USED_COUPON_EXCEPTION;
import static com.firstone.greenjangteo.coupon.testutil.CouponTestConstant.*;
import static com.firstone.greenjangteo.user.model.Role.ROLE_BUYER;
import static com.firstone.greenjangteo.user.testutil.UserTestConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
class CouponServiceTest {
    @Autowired
    private CouponService couponService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponGroupRepository couponGroupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EntityManager entityManager;

    private final LocalDate today = LocalDate.now();
    private final LocalDate tomorrow = LocalDate.now().plusDays(1);

    @AfterEach
    void tearDown() {
        couponRepository.deleteAll();
        couponGroupRepository.deleteAll();
    }

    @DisplayName("올바른 쿠폰 발행 양식을 전송하면 대량의 쿠폰을 등록할 수 있다.")
    @Test
    void createCoupons() throws JobExecutionException {
        // given
        IssueCouponsRequestDto issueCouponsRequestDto
                = CouponTestObjectFactory.createIssueCouponsRequestDto(
                COUPON_NAME1, AMOUNT, DESCRIPTION, ISSUE_QUANTITY1, tomorrow, EXPIRATION_PERIOD1
        );

        // when
        couponService.createCoupons(issueCouponsRequestDto);
        CouponGroup couponGroup = couponGroupRepository.findByCouponName(COUPON_NAME1).get();
        List<Coupon> coupons = couponRepository.findAll();

        // then
        assertThat(couponGroup.getCouponName()).isEqualTo(COUPON_NAME1);
        assertThat(couponGroup.getAmount()).isEqualTo(Amount.of(AMOUNT));
        assertThat(couponGroup.getDescription()).isEqualTo(DESCRIPTION);
        assertThat(couponGroup.getIssueQuantity()).isEqualTo(IssueQuantity.of(ISSUE_QUANTITY1));
        assertThat(couponGroup.getScheduledIssueDate()).isEqualTo(tomorrow);
        assertThat(couponGroup.getExpirationPeriod()).isEqualTo(ExpirationPeriod.of(EXPIRATION_PERIOD1));
        assertThat(coupons).hasSize(Integer.parseInt(ISSUE_QUANTITY1));
    }

    @DisplayName("발행 매수가 -1이면 쿠폰 그룹만 생성한다.")
    @Test
    void createCouponsWhenIssueQuantityIsMinusOne() throws JobExecutionException {
        // given
        IssueCouponsRequestDto issueCouponsRequestDto
                = CouponTestObjectFactory.createIssueCouponsRequestDto(
                COUPON_NAME1, AMOUNT, DESCRIPTION, MINUS_ONE_ISSUE_QUANTITY, tomorrow, EXPIRATION_PERIOD1
        );

        // when
        couponService.createCoupons(issueCouponsRequestDto);
        CouponGroup couponGroup = couponGroupRepository.findByCouponName(COUPON_NAME1).get();
        List<Coupon> coupons = couponRepository.findAll();

        // then
        assertThat(couponGroup.getCouponName()).isEqualTo(COUPON_NAME1);
        assertThat(couponGroup.getAmount()).isEqualTo(Amount.of(AMOUNT));
        assertThat(couponGroup.getDescription()).isEqualTo(DESCRIPTION);
        assertThat(couponGroup.getIssueQuantity()).isEqualTo(IssueQuantity.of(MINUS_ONE_ISSUE_QUANTITY));
        assertThat(couponGroup.getScheduledIssueDate()).isEqualTo(tomorrow);
        assertThat(couponGroup.getExpirationPeriod()).isEqualTo(ExpirationPeriod.of(EXPIRATION_PERIOD1));
        assertThat(coupons).isEmpty();
    }

    @DisplayName("대량의 쿠폰을 발행할 수 있다.")
    @Test
    void issueCoupons() throws JobExecutionException {
        // given
        IssueCouponsRequestDto issueCouponsRequestDto
                = CouponTestObjectFactory.createIssueCouponsRequestDto(
                COUPON_NAME1, AMOUNT, DESCRIPTION, ISSUE_QUANTITY1, today, EXPIRATION_PERIOD1
        );
        couponService.createCoupons(issueCouponsRequestDto);
        LocalDateTime beforeIssue = LocalDateTime.now();

        // when
        couponService.issueCoupons();
        List<Coupon> coupons = couponRepository.findAll();

        // then
        LocalDateTime afterIssue = LocalDateTime.now().plusNanos(1);

        for (Coupon coupon : coupons) {
            LocalDateTime modifiedAt = coupon.getModifiedAt();
            LocalDateTime issuedAt = coupon.getIssuedAt();
            LocalDateTime expiredAt = coupon.getExpiredAt();

            assertThat(modifiedAt).isAfter(beforeIssue);
            assertThat(modifiedAt).isBefore(afterIssue);
            assertThat(issuedAt).isAfter(beforeIssue);
            assertThat(issuedAt).isBefore(afterIssue);
            assertThat(expiredAt).isEqualTo(issuedAt.plusDays(Long.parseLong(EXPIRATION_PERIOD1)));
        }
    }

    @DisplayName("대량의 쿠폰을 회원에게 지급할 수 있다.")
    @Test
    @DirtiesContext
    void provideCoupons() throws JobExecutionException {
        // given
        IssueCouponsRequestDto issueCouponsRequestDto
                = CouponTestObjectFactory.createIssueCouponsRequestDto(
                COUPON_NAME1, AMOUNT, DESCRIPTION, "3", today, EXPIRATION_PERIOD1
        );
        couponService.createCoupons(issueCouponsRequestDto);
        couponService.issueCoupons();

        Long couponGroupId = couponGroupRepository.findByCouponName(COUPON_NAME1).get().getId();

        User user1 = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.name())
        );
        User user2 = UserTestObjectFactory.createUser(
                EMAIL2, USERNAME2, PASSWORD2, passwordEncoder, FULL_NAME2, PHONE2, List.of(ROLE_BUYER.name())
        );
        User user3 = UserTestObjectFactory.createUser(
                EMAIL3, USERNAME3, PASSWORD3, passwordEncoder, FULL_NAME3, PHONE3, List.of(ROLE_BUYER.name())
        );
        userRepository.saveAll(List.of(user1, user2, user3));

        List<Long> userIds = userRepository.findAllUserIds();
        int quantity = (int) userRepository.count();

        ProvideCouponsToUsersRequestDto provideCouponsToUsersRequestDto
                = new ProvideCouponsToUsersRequestDto(couponGroupId, userIds, quantity);

        // when
        couponService.provideCouponsToUsers(provideCouponsToUsersRequestDto);

        List<Coupon> coupons1 = couponRepository.findAllByUserId(user1.getId());
        List<Coupon> coupons2 = couponRepository.findAllByUserId(user2.getId());
        List<Coupon> coupons3 = couponRepository.findAllByUserId(user3.getId());

        // then
        assertThat(coupons1).hasSize(1)
                .extracting("user")
                .isNotNull();

        assertThat(coupons2).hasSize(1)
                .extracting("user")
                .isNotNull();

        assertThat(coupons3).hasSize(1)
                .extracting("user")
                .isNotNull();
    }

    @DisplayName("유효기간이 오늘 이전인 대량의 쿠폰을 삭제할 수 있다.")
    @Test
    void deleteExpiredCoupons() throws JobExecutionException {
        // given
        CouponGroup couponGroup1
                = CouponTestObjectFactory.createCouponGroup(
                COUPON_NAME1, AMOUNT, DESCRIPTION, ISSUE_QUANTITY1, tomorrow, EXPIRATION_PERIOD1
        );
        CouponGroup couponGroup2
                = CouponTestObjectFactory.createCouponGroup(
                COUPON_NAME2, AMOUNT, DESCRIPTION, ISSUE_QUANTITY2, tomorrow, EXPIRATION_PERIOD2
        );

        List<Coupon> coupons1 = CouponTestObjectFactory.createAndIssueCoupons(couponGroup1, LocalDateTime.now());
        List<Coupon> coupons2
                = CouponTestObjectFactory.createAndIssueCoupons(couponGroup2, LocalDateTime.now().plusDays(1));

        couponGroupRepository.saveAll(List.of(couponGroup1, couponGroup2));
        couponRepository.saveAll(coupons1);
        couponRepository.saveAll(coupons2);

        // when
        couponService.deleteExpiredCoupons();
        List<Coupon> foundCoupons = couponRepository.findAll();

        // then
        assertThat(foundCoupons).hasSize(coupons2.size());
    }

    @DisplayName("미리 발급된 쿠폰을 전송된 수량만큼 회원에게 지급할 수 있다.")
    @Test
    @Transactional
    void provideCouponsToUserWhenSufficient() {
        prepareAndProvideCouponsToUser(ISSUE_QUANTITY3);
    }

    @DisplayName("전송된 수량에 비해 발급된 쿠폰이 부족하면, 부족한 수량만큼 새로운 쿠폰을 발급해 회원에게 지급할 수 있다.")
    @Test
    @Transactional
    void createAndProvideCouponsToUserWhenInsufficient() {
        prepareAndProvideCouponsToUser(ISSUE_QUANTITY4);
    }

    @DisplayName("회원 ID를 통해 쿠폰 목록을 검색할 수 있다.")
    @Test
    @Transactional
    void getCoupons() {
        // given
        User user = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.toString())
        );
        userRepository.save(user);

        CouponGroup couponGroup1 = CouponTestObjectFactory.createCouponGroup(
                COUPON_NAME1, AMOUNT, DESCRIPTION, ISSUE_QUANTITY1, tomorrow, EXPIRATION_PERIOD1
        );

        CouponGroup couponGroup2 = CouponTestObjectFactory.createCouponGroup(
                COUPON_NAME2, AMOUNT, DESCRIPTION, ISSUE_QUANTITY2, tomorrow, EXPIRATION_PERIOD2
        );

        List<Coupon> coupons1 = CouponTestObjectFactory.createCoupons(couponGroup1);
        List<Coupon> coupons2 = CouponTestObjectFactory.createAndProvideCoupons(couponGroup2, user);

        couponGroupRepository.saveAll(List.of(couponGroup1, couponGroup2));
        couponRepository.saveAll(coupons1);
        couponRepository.saveAll(coupons2);

        // when
        List<Coupon> foundCoupons = couponService.getCoupons(user.getId());

        // then
        assertThat(foundCoupons).hasSize(coupons2.size())
                .extracting("couponGroup")
                .containsOnly(couponGroup2);
    }

    @DisplayName("쿠폰을 주문에 사용할 수 있다.")
    @Test
    @Transactional
    void updateUsedCoupon() {
        // given
        Long orderId = 1L;

        CouponGroup couponGroup = CouponTestObjectFactory.createCouponGroup(
                COUPON_NAME1, AMOUNT, DESCRIPTION, ISSUE_QUANTITY1, tomorrow, EXPIRATION_PERIOD1
        );
        couponGroupRepository.save(couponGroup);

        List<Coupon> coupons = CouponTestObjectFactory.createCoupons(couponGroup);
        couponRepository.saveAll(coupons);

        Coupon coupon1 = coupons.get(0);
        Coupon coupon2 = coupons.get(1);

        // when
        couponService.updateUsedCoupon(orderId, coupon1.getId());

        // then
        assertThat(coupon1.getUsedOrderId()).isEqualTo(orderId);
        assertThat(coupon2.getUsedOrderId()).isNull();
    }

    @DisplayName("이미 사용된 쿠폰을 사용하려 하면 AlreadyUsedCouponException이 발생한다.")
    @Test
    @Transactional
    void updateAlreadyUsedCoupon() {
        // given
        Long orderId1 = 1L;
        Long orderId2 = 2L;

        CouponGroup couponGroup = CouponTestObjectFactory.createCouponGroup(
                COUPON_NAME1, AMOUNT, DESCRIPTION, ISSUE_QUANTITY1, tomorrow, EXPIRATION_PERIOD1
        );
        couponGroupRepository.save(couponGroup);

        List<Coupon> coupons = CouponTestObjectFactory.createCoupons(couponGroup);
        couponRepository.saveAll(coupons);

        Coupon coupon = coupons.get(0);

        couponService.updateUsedCoupon(orderId1, coupon.getId());

        // when, then
        assertThatThrownBy(() -> couponService.updateUsedCoupon(orderId2, coupon.getId()))
                .isInstanceOf(AlreadyUsedCouponException.class)
                .hasMessage(ALREADY_USED_COUPON_EXCEPTION + orderId1);
    }

    @DisplayName("쿠폰 ID를 통해 쿠폰을 삭제할 수 있다.")
    @Test
    void deleteCoupon() {
        // given
        CouponGroup couponGroup
                = CouponTestObjectFactory.createCouponGroup(
                COUPON_NAME1, AMOUNT, DESCRIPTION, ISSUE_QUANTITY1, tomorrow, EXPIRATION_PERIOD1
        );
        Coupon coupon = new Coupon(couponGroup, LocalDateTime.now());

        couponGroupRepository.save(couponGroup);
        couponRepository.save(coupon);

        Long couponId = coupon.getId();

        // when
        couponService.deleteCoupon(couponId);
        boolean result = couponRepository.existsById(couponId);

        // then
        assertThat(result).isFalse();
    }

    private void prepareAndProvideCouponsToUser(String issueQuantity) {
        // given
        User user = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.toString())
        );
        userRepository.save(user);

        CouponGroup createdCouponGroup = CouponTestObjectFactory.createCouponGroup(
                COUPON_NAME1, AMOUNT, DESCRIPTION, issueQuantity, tomorrow, EXPIRATION_PERIOD1
        );

        List<Coupon> coupons = CouponTestObjectFactory.createCoupons(createdCouponGroup);
        CouponGroup savedCouponGroup = couponGroupRepository.save(createdCouponGroup);
        couponRepository.saveAll(coupons);
        entityManager.refresh(savedCouponGroup);

        ProvideCouponsToUserRequestDto provideCouponsToUserRequestDto = new ProvideCouponsToUserRequestDto(
                user, COUPON_NAME1, Integer.parseInt(QUANTITY_TO_PROVIDE)
        );

        LocalDateTime beforeGiving = LocalDateTime.now();

        // when
        couponService.provideCouponsToUser(provideCouponsToUserRequestDto);

        // then
        LocalDateTime afterGiving = LocalDateTime.now().plusNanos(1);
        int providedQuantity = Integer.parseInt(QUANTITY_TO_PROVIDE);
        List<Coupon> userAddedCoupons = savedCouponGroup.getCoupons();

        for (int i = 0; i < providedQuantity; i++) {
            LocalDateTime issuedAt = userAddedCoupons.get(0).getIssuedAt();
            LocalDateTime expiredAt = userAddedCoupons.get(0).getExpiredAt();

            assertThat(userAddedCoupons.get(i).getUser()).isEqualTo(user);
            assertThat(issuedAt).isAfter(beforeGiving);
            assertThat(issuedAt).isBefore(afterGiving);
            assertThat(expiredAt).isEqualTo(issuedAt.plusDays(Long.parseLong(EXPIRATION_PERIOD1)));
        }
    }
}
