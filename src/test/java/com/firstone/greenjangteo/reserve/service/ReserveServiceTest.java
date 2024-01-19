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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.firstone.greenjangteo.reserve.testutil.ReserveTestConstant.RESERVE1;
import static com.firstone.greenjangteo.reserve.testutil.ReserveTestConstant.RESERVE2;
import static com.firstone.greenjangteo.user.model.Role.ROLE_BUYER;
import static com.firstone.greenjangteo.user.testutil.UserTestConstant.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("test")
@SpringBootTest
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
    void useReserve() {
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
}