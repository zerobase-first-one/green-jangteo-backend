package com.firstone.greenjangteo.user.service;

import com.firstone.greenjangteo.user.model.entity.User;
import com.firstone.greenjangteo.user.repository.UserRepository;
import com.firstone.greenjangteo.user.testutil.TestObjectFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static com.firstone.greenjangteo.user.excpeption.message.NotFoundExceptionMessage.USER_ID_NOT_FOUND_EXCEPTION;
import static com.firstone.greenjangteo.user.model.Role.ROLE_BUYER;
import static com.firstone.greenjangteo.user.testutil.TestConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class UserServiceTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @DisplayName("사용자 이름을 통해 회원 개인 정보를 조회할 수 있다.")
    @ParameterizedTest
    @CsvSource({
            "abcd@abc.com, person1, Abcd1234!, 홍길동, 01012345678, ROLE_BUYER",
            "abcd@abcd.com, person2, Abcd12345!, 고길동, 01012345679, ROLE_SELLER",
            "abcd@abcde.com, person3, Abcd123456!, 김길동, 01012345680, ROLE_ADMIN"
    })
    void getUserDetails(String email, String username, String password,
                        String fullName, String phone, String role) {

        // given
        User user = TestObjectFactory.createUser(
                email, username, password, passwordEncoder, fullName, phone, List.of(role)
        );

        userRepository.save(user);

        // when
        User foundUser = userService.getUser(user.getId());

        // then
        assertThat(foundUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(foundUser.getUsername()).isEqualTo(user.getUsername());
        assertThat(foundUser.getPhone()).isEqualTo(user.getPhone());
        assertThat(foundUser.getRoles()).isEqualTo(user.getRoles());
    }

    @DisplayName("잘못된 ID로 회원 개인 정보를 조회하면 EntityNotFoundException이 발생한다.")
    @Test
    void getUserDetailsByWrongUserId() {
        // given
        User user = TestObjectFactory.createUser(EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1,
                PHONE1, List.of(ROLE_BUYER.toString()));

        userRepository.save(user);

        // when, then
        assertThatThrownBy(() -> userService.getUser(user.getId() + 1))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(USER_ID_NOT_FOUND_EXCEPTION + (user.getId() + 1));
    }
}
