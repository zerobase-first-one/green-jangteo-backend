package com.firstone.greenjangteo.coupon.service;

import com.firstone.greenjangteo.coupon.dto.request.IssueCouponsRequestDto;
import com.firstone.greenjangteo.coupon.dto.request.ProvideCouponsToUserRequestDto;
import com.firstone.greenjangteo.coupon.dto.request.ProvideCouponsToUsersRequestDto;
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
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.firstone.greenjangteo.coupon.excpeption.message.NotFoundExceptionMessage.COUPON_GROUP_ID_NOT_FOUND_EXCEPTION;
import static com.firstone.greenjangteo.coupon.excpeption.message.NotFoundExceptionMessage.COUPON_NAME_NOT_FOUND_EXCEPTION;
import static com.firstone.greenjangteo.coupon.testutil.CouponTestConstant.*;
import static com.firstone.greenjangteo.user.model.Role.ROLE_BUYER;
import static com.firstone.greenjangteo.user.testutil.UserTestConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

@ActiveProfiles("test")
@SpringBootTest
class CouponServiceTest {
    @Autowired
    private CouponService couponService;

    @Autowired
    private AuthenticationService authenticationService;

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

    private final LocalDate today = LocalDate.now();
    private final LocalDate tomorrow = LocalDate.now().plusDays(1);

    @AfterEach
    void tearDown() {
        couponGroupRepository.deleteAll();
        couponRepository.deleteAll();
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

    @DisplayName("쿠폰 그룹 ID를 통해 쿠폰 목록을 페이징 처리한 쿠폰 그룹을 찾을 수 있다.")
    @Test
    @Transactional
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
        List<Coupon> foundCoupons = couponService.getCouponGroup(createdCouponGroup.getId(), Pageable.ofSize(2))
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
    @Transactional
    void getCouponGroupByNonExistentCouponGroupId() {
        // given
        Long couponGroupId = 1L;
        Pageable pageable = mock(Pageable.class);

        // when, then
        assertThatThrownBy(() -> couponService.getCouponGroup(couponGroupId, pageable))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(COUPON_GROUP_ID_NOT_FOUND_EXCEPTION + couponGroupId);
    }

    @DisplayName("쿠폰 이름을 통해 쿠폰 그룹을 찾을 수 있다.")
    @Test
    @Transactional
    void getCouponGroupByCouponName() {
        // given
        CouponGroup createdCouponGroup
                = CouponTestObjectFactory.createCouponGroup(
                COUPON_NAME1, AMOUNT, DESCRIPTION, ISSUE_QUANTITY1, tomorrow, EXPIRATION_PERIOD1
        );
        couponGroupRepository.save(createdCouponGroup);

        // when
        CouponGroup foundCouponGroup = couponService.getCouponGroup(COUPON_NAME1);

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
    @Transactional
    void getCouponGroupByNonExistentCouponName() {
        // given, when, then
        assertThatThrownBy(() -> couponService.getCouponGroup(COUPON_NAME1))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(COUPON_NAME_NOT_FOUND_EXCEPTION + COUPON_NAME1);
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