package com.firstone.greenjangteo.reserve.service;

import com.firstone.greenjangteo.reserve.dto.request.AddReserveRequestDto;
import com.firstone.greenjangteo.reserve.dto.request.UseReserveRequestDto;
import com.firstone.greenjangteo.reserve.model.CurrentReserve;
import com.firstone.greenjangteo.reserve.model.entity.ReserveHistory;
import com.firstone.greenjangteo.reserve.repository.ReserveHistoryRepository;
import com.firstone.greenjangteo.reserve.testutil.ReserveTestObjectFactory;
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

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static com.firstone.greenjangteo.reserve.exception.message.NotFoundExceptionMessage.RESERVE_NOT_FOUND_EXCEPTION;
import static com.firstone.greenjangteo.reserve.testutil.ReserveTestConstant.RESERVE1;
import static com.firstone.greenjangteo.reserve.testutil.ReserveTestConstant.RESERVE2;
import static com.firstone.greenjangteo.user.model.Role.ROLE_BUYER;
import static com.firstone.greenjangteo.user.testutil.UserTestConstant.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class ReserveServiceTest {
    @Autowired
    private ReserveService reserveService;

    @Autowired
    private ReserveHistoryRepository reserveHistoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @DisplayName("회원의 적립금 내역을 생성할 수 있다.")
    @Test
    void createReserveHistory() {
        // given
        User user = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.toString())
        );
        userRepository.save(user);

        AddReserveRequestDto addReserveRequestDto
                = ReserveTestObjectFactory.createAddReserveRequestDto(user.getId().toString(), RESERVE1);

        // when
        reserveService.addReserve(addReserveRequestDto);

        // then
        ReserveHistory reserveHistory
                = reserveHistoryRepository.findFirstByUserIdOrderByCreatedAtDesc(user.getId()).get();

        assertThat(reserveHistory.getCurrentReserve()).isEqualTo(new CurrentReserve(RESERVE1));
    }

    @DisplayName("회원의 적립금을 추가할 수 있다.")
    @Test
    void addReserve() {
        // given
        User user = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.toString())
        );
        userRepository.save(user);

        AddReserveRequestDto addReserveRequestDto1
                = ReserveTestObjectFactory.createAddReserveRequestDto(user.getId().toString(), RESERVE1);
        ReserveHistory firstReserveHistory = ReserveHistory.from(addReserveRequestDto1, new CurrentReserve(0));
        reserveHistoryRepository.save(firstReserveHistory);

        AddReserveRequestDto addReserveRequestDto2
                = ReserveTestObjectFactory.createAddReserveRequestDto(user.getId().toString(), RESERVE2);

        // when
        reserveService.addReserve(addReserveRequestDto2);

        // then
        ReserveHistory currentReserveHistory
                = reserveHistoryRepository.findFirstByUserIdOrderByCreatedAtDesc(user.getId()).get();

        assertThat(currentReserveHistory.getCurrentReserve()).isEqualTo(new CurrentReserve(RESERVE1 + RESERVE2));
    }

    @DisplayName("회원의 적립금을 차감할 수 있다.")
    @Test
    void reduceReserve() {
        // given
        User user = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.toString())
        );
        userRepository.save(user);

        ReserveHistory firstReserveHistory
                = ReserveTestObjectFactory.createReserveHistory(user.getId(), RESERVE1, new CurrentReserve(RESERVE2));
        reserveHistoryRepository.save(firstReserveHistory);

        UseReserveRequestDto useReserveRequestDto
                = ReserveTestObjectFactory.createUseReserveRequestDto(user.getId().toString(), RESERVE1);

        // when
        reserveService.reduceReserve(useReserveRequestDto);

        // then
        ReserveHistory currentReserveHistory
                = reserveHistoryRepository.findFirstByUserIdOrderByCreatedAtDesc(user.getId()).get();

        assertThat(currentReserveHistory.getCurrentReserve()).isEqualTo(new CurrentReserve(RESERVE2));
    }

    @DisplayName("회원의 적립금을 사용할 수 있다.")
    @Test
    void UseReserve() {
        // given
        User user = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.toString())
        );
        userRepository.save(user);

        ReserveHistory firstReserveHistory
                = ReserveTestObjectFactory.createReserveHistory(user.getId(), RESERVE1, new CurrentReserve(RESERVE2));
        reserveHistoryRepository.save(firstReserveHistory);

        UseReserveRequestDto useReserveRequestDto
                = ReserveTestObjectFactory.createUseReserveRequestDto(user.getId().toString(), RESERVE1);

        Long orderId = 1L;

        // when
        reserveService.useReserve(orderId, useReserveRequestDto);

        // then
        ReserveHistory currentReserveHistory
                = reserveHistoryRepository.findFirstByUserIdOrderByCreatedAtDesc(user.getId()).get();

        assertThat(currentReserveHistory.getCurrentReserve()).isEqualTo(new CurrentReserve(RESERVE2));
    }

    @DisplayName("회원이 사용한 적립금을 되돌릴 수 있다.")
    @Test
    void rollBackUsedReserve() {
        // given
        User user = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.toString())
        );
        userRepository.save(user);

        ReserveHistory firstReserveHistory
                = ReserveTestObjectFactory.createReserveHistory(user.getId(), RESERVE1, new CurrentReserve(RESERVE2));
        reserveHistoryRepository.save(firstReserveHistory);

        UseReserveRequestDto useReserveRequestDto
                = ReserveTestObjectFactory.createUseReserveRequestDto(user.getId().toString(), RESERVE1);

        Long orderId = 1L;

        reserveService.useReserve(orderId, useReserveRequestDto);

        AddReserveRequestDto addReserveRequestDto
                = ReserveTestObjectFactory.createAddReserveRequestDto(user.getId().toString(), RESERVE1);

        // when
        reserveService.rollBackUsedReserve(orderId, addReserveRequestDto);

        // then
        ReserveHistory currentReserveHistory
                = reserveHistoryRepository.findFirstByUserIdOrderByCreatedAtDesc(user.getId()).get();

        assertThat(currentReserveHistory.getCurrentReserve()).isEqualTo(new CurrentReserve(RESERVE1 + RESERVE2));
    }

    @DisplayName("회원 ID를 통해 적립금 내역을 생성시간 오름차순으로 조회할 수 있다.")
    @Test
    void getReserveHistories() {
        // given
        User user = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.toString())
        );
        userRepository.save(user);

        ReserveHistory reserveHistory1
                = ReserveTestObjectFactory.createReserveHistory(user.getId(), RESERVE1, new CurrentReserve(RESERVE2));
        ReserveHistory reserveHistory2 = ReserveTestObjectFactory
                .createReserveHistory(user.getId(), RESERVE1, new CurrentReserve(RESERVE1 + RESERVE2));
        reserveHistoryRepository.saveAll(List.of(reserveHistory1, reserveHistory2));

        // when
        List<ReserveHistory> reserveHistories = reserveService.getReserveHistories(user.getId());

        // then
        Assertions.assertThat(reserveHistories).hasSize(2)
                .extracting("addedReserve", "currentReserve")
                .containsExactly(
                        tuple(RESERVE1, new CurrentReserve(2 * RESERVE1 + RESERVE2)),
                        tuple(RESERVE1, new CurrentReserve(RESERVE1 + RESERVE2))
                );
    }

    @DisplayName("회원 ID를 통해 가장 최신의 적립금 내역을 조회할 수 있다.")
    @Test
    void getCurrentReserve() {
        User user = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.toString())
        );
        userRepository.save(user);

        ReserveHistory reserveHistory1
                = ReserveTestObjectFactory.createReserveHistory(user.getId(), RESERVE1, new CurrentReserve(RESERVE2));
        ReserveHistory reserveHistory2 = ReserveTestObjectFactory
                .createReserveHistory(user.getId(), RESERVE1, new CurrentReserve(RESERVE1 + RESERVE2));
        reserveHistoryRepository.saveAll(List.of(reserveHistory1, reserveHistory2));

        // when
        ReserveHistory foundReserveHistory
                = reserveService.getCurrentReserve(user.getId());

        // then
        assertThat(foundReserveHistory.getAddedReserve()).isEqualTo(reserveHistory2.getAddedReserve());
        assertThat(foundReserveHistory.getCurrentReserve()).isEqualTo(reserveHistory2.getCurrentReserve());
        assertThat(foundReserveHistory.getCurrentReserve().getValue()).isEqualTo(2 * RESERVE1 + RESERVE2);
    }

    @DisplayName("잘못된 회원 ID를 통해 가장 최신의 적립금 내역을 조회하면 EntityNotFoundException이 발생한다.")
    @Test
    void getCurrentReserveFromWrongUserId() {
        User user = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.toString())
        );
        userRepository.save(user);

        ReserveHistory reserveHistory
                = ReserveTestObjectFactory.createReserveHistory(user.getId(), RESERVE1, new CurrentReserve(RESERVE2));
        reserveHistoryRepository.save(reserveHistory);

        Long requestedUserId = user.getId() + 1;

        // when, then
        assertThatThrownBy(() -> reserveService.getCurrentReserve(requestedUserId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(RESERVE_NOT_FOUND_EXCEPTION + requestedUserId);
    }
}
