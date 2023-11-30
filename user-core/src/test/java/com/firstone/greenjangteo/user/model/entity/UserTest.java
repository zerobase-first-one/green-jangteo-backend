package com.firstone.greenjangteo.user.model.entity;

import com.firstone.greenjangteo.user.form.SignUpForm;
import com.firstone.greenjangteo.user.model.Email;
import com.firstone.greenjangteo.user.model.FullName;
import com.firstone.greenjangteo.user.model.Phone;
import com.firstone.greenjangteo.user.model.Username;
import com.firstone.greenjangteo.user.model.embedment.Roles;
import com.firstone.greenjangteo.user.testutil.TestObjectFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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
        SignUpForm signUpForm = TestObjectFactory.enterUserForm(
                email, username, password, fullName, phone, List.of(role)
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
}