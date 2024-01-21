package com.firstone.greenjangteo.reserve.repository;

import com.firstone.greenjangteo.reserve.model.CurrentReserve;
import com.firstone.greenjangteo.reserve.model.entity.ReserveHistory;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.firstone.greenjangteo.reserve.testutil.ReserveTestConstant.RESERVE1;
import static com.firstone.greenjangteo.reserve.testutil.ReserveTestConstant.RESERVE2;
import static com.firstone.greenjangteo.user.model.Role.ROLE_BUYER;
import static com.firstone.greenjangteo.user.testutil.UserTestConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class ReserveHistoryRepositoryTest {
    @Autowired
    private ReserveHistoryRepository reserveHistoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @DisplayName("회원 ID를 통해 가장 최신의 적립금 내역을 조회할 수 있다.")
    @Test
    void findFirstByUserIdOrderByCreatedAtDesc() {
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
        ReserveHistory foundReserveHistory
                = reserveHistoryRepository.findFirstByUserIdOrderByCreatedAtDesc(user.getId()).get();

        // then
        assertThat(foundReserveHistory.getAddedReserve()).isEqualTo(reserveHistory2.getAddedReserve());
        assertThat(foundReserveHistory.getCurrentReserve()).isEqualTo(reserveHistory2.getCurrentReserve());
        assertThat(foundReserveHistory.getCurrentReserve().getValue()).isEqualTo(2 * RESERVE1 + RESERVE2);
    }

    @DisplayName("회원 ID를 통해 적립금 내역을 생성시간 오름차순으로 검색할 수 있다.")
    @Test
    void findByUserIdOrderByCreatedAtDesc() {
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
        List<ReserveHistory> reserveHistories = reserveHistoryRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        // then
        assertThat(reserveHistories).hasSize(2)
                .extracting("addedReserve", "currentReserve")
                .containsExactly(
                        tuple(RESERVE1, new CurrentReserve(2 * RESERVE1 + RESERVE2)),
                        tuple(RESERVE1, new CurrentReserve(RESERVE1 + RESERVE2))
                );
    }
}
