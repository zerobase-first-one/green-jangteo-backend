package com.firstone.greenjangteo.user.model.entity;

import com.firstone.greenjangteo.user.form.SignUpForm;
import com.firstone.greenjangteo.user.model.Email;
import com.firstone.greenjangteo.user.model.FullName;
import com.firstone.greenjangteo.user.model.Phone;
import com.firstone.greenjangteo.user.model.Username;
import com.firstone.greenjangteo.user.model.embedment.Roles;
import com.firstone.greenjangteo.user.testutil.UserTestObjectFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.firstone.greenjangteo.user.model.Role.ROLE_BUYER;
import static com.firstone.greenjangteo.user.testutil.UserTestConstant.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class UserTest {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @DisplayName("올바른 값을 전송하면 회원 인스턴스를 생성할 수 있다.")
    @ParameterizedTest
    @CsvSource({
            "abcd@abc.com, person1, Abcd1234!, 홍길동, 01012345678, ROLE_BUYER",
            "abcd@abcd.com, person2, Abcd12345!, 고길동, 01012345679, ROLE_SELLER",
            "abcd@abcde.com, person3, Abcd123456!, 김길동, 01012345680, ROLE_ADMIN"
    })
    void from(String email, String username, String password, String fullName,
              String phone, String role) {
        // given
        SignUpForm signUpForm = UserTestObjectFactory.enterUserForm(
                email, username, password, password, fullName, phone, List.of(role)
        );

        // when
        User user = User.from(signUpForm, passwordEncoder);

        // then
        assertThat(user.getEmail()).isEqualTo(Email.of(email));
        assertThat(user.getUsername()).isEqualTo(Username.of(username));
        assertThat(user.getPassword().matchOriginalPassword(passwordEncoder, password)).isTrue();
        assertThat(user.getFullName()).isEqualTo(FullName.of(fullName));
        assertThat(user.getPhone()).isEqualTo(Phone.of(phone));
        assertThat(user.getRoles()).isEqualTo(Roles.from(List.of(role)));
    }

    @DisplayName("동일한 내부 값들을 전송하면 동등한 User 인스턴스를 생성한다.")
    @Test
    void fromSameValue() {
        // given, when
        User user1 = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.toString())
        );

        User user2 = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.toString())
        );

        // then
        assertThat(user1).isEqualTo(user2);
        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
    }

    @DisplayName("다른 내부 값들을 전송하면 동등하지 않은 User 인스턴스를 생성한다.")
    @Test
    void fromDifferentValue() {
        // given, when
        User user1 = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.toString())
        );

        User user2 = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME2, PHONE1, List.of(ROLE_BUYER.toString())
        );

        // then
        assertThat(user1).isNotEqualTo(user2);
        assertThat(user1.hashCode()).isNotEqualTo(user2.hashCode());
    }
}