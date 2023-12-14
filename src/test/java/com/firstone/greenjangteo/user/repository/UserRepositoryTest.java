package com.firstone.greenjangteo.user.repository;

import com.firstone.greenjangteo.user.model.Email;
import com.firstone.greenjangteo.user.model.Phone;
import com.firstone.greenjangteo.user.model.Username;
import com.firstone.greenjangteo.user.model.entity.User;
import com.firstone.greenjangteo.user.testutil.UserTestObjectFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.firstone.greenjangteo.user.model.Role.ROLE_BUYER;
import static com.firstone.greenjangteo.user.testutil.UserTestConstant.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @DisplayName("대상 이메일 주소의 존재 여부를 확인할 수 있다.")
    @Test
    void existsByEmail() {
        // given
        Email email1 = Email.of(EMAIL1);
        Email email2 = Email.of(EMAIL2);
        User user = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.toString())
        );

        userRepository.save(user);

        // when
        boolean result1 = userRepository.existsByEmail(email1);
        boolean result2 = userRepository.existsByEmail(email2);

        // then
        assertThat(result1).isTrue();
        assertThat(result2).isFalse();
    }

    @DisplayName("대상 사용자 이름의 존재 여부를 확인할 수 있다.")
    @Test
    void existsByUsername() {
        // given
        Username username1 = Username.of(USERNAME1);
        Username username2 = Username.of(USERNAME2);
        User user = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.toString())
        );

        userRepository.save(user);

        // when
        boolean result1 = userRepository.existsByUsername(username1);
        boolean result2 = userRepository.existsByUsername(username2);

        // then
        assertThat(result1).isTrue();
        assertThat(result2).isFalse();
    }

    @DisplayName("대상 전화번호의 존재 여부를 확인할 수 있다.")
    @Test
    void existsByPhone() {
        // given
        Phone phone1 = Phone.of(PHONE1);
        Phone phone2 = Phone.of(PHONE2);
        User user = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.toString())
        );

        userRepository.save(user);

        // when
        boolean result1 = userRepository.existsByPhone(phone1);
        boolean result2 = userRepository.existsByPhone(phone2);

        // then
        assertThat(result1).isTrue();
        assertThat(result2).isFalse();
    }

    @DisplayName("저장된 이메일 주소를 통해 회원을 찾을 수 있다.")
    @Test
    void findByEmailByExistent() {
        // given
        User user = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.toString())
        );

        User savedUser = userRepository.save(user);

        // when
        Optional<User> foundUser = userRepository.findByEmail(Email.of(EMAIL1));

        // then
        assertThat(foundUser.get().getId()).isEqualTo(savedUser.getId());
        assertThat(foundUser.get().getEmail()).isEqualTo(Email.of(EMAIL1));
        assertThat(foundUser.get().getPassword().matchOriginalPassword(passwordEncoder, PASSWORD1)).isTrue();
    }

    @DisplayName("저장되지 않은 이메일 주소로 회원을 검색하면 회원을 반환하지 않는다.")
    @Test
    void findByEmailByNotExistent() {
        // given
        UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.toString())
        );

        // when
        Optional<User> foundUser = userRepository.findByEmail(Email.of(EMAIL1));

        // then
        assertThat(foundUser).isEmpty();
    }

    @DisplayName("저장된 사용자 이름을 통해 회원을 찾을 수 있다.")
    @Test
    void findByUsernameByExistent() {
        // given
        User user = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.toString())
        );

        User savedUser = userRepository.save(user);

        // when
        Optional<User> foundUser = userRepository.findByUsername(Username.of(USERNAME1));

        // then
        assertThat(foundUser.get().getId()).isEqualTo(savedUser.getId());
        assertThat(foundUser.get().getUsername()).isEqualTo(Username.of(USERNAME1));
        assertThat(foundUser.get().getPassword().matchOriginalPassword(passwordEncoder, PASSWORD1)).isTrue();
    }

    @DisplayName("저장되지 않은 사용자 이름으로 회원을 검색하면 회원을 반환하지 않는다.")
    @Test
    void findByUsernameByNotExistent() {
        // given
        UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.toString())
        );

        // when
        Optional<User> foundUser = userRepository.findByUsername(Username.of(USERNAME1));

        // then
        assertThat(foundUser).isEmpty();
    }
}
