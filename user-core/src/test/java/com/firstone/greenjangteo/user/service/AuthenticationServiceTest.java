package com.firstone.greenjangteo.user.service;

import com.firstone.greenjangteo.user.excpeption.general.DuplicateUserException;
import com.firstone.greenjangteo.user.excpeption.general.DuplicateUsernameException;
import com.firstone.greenjangteo.user.form.SignUpForm;
import com.firstone.greenjangteo.user.model.Email;
import com.firstone.greenjangteo.user.model.Phone;
import com.firstone.greenjangteo.user.model.Role;
import com.firstone.greenjangteo.user.model.Username;
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

import java.util.List;

import static com.firstone.greenjangteo.user.excpeption.message.DuplicateExceptionMessage.*;
import static com.firstone.greenjangteo.user.model.Role.ROLE_BUYER;
import static com.firstone.greenjangteo.user.model.Role.ROLE_SELLER;
import static com.firstone.greenjangteo.user.testutil.TestConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class AuthenticationServiceTest {
    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @DisplayName("올바른 회원 가입 양식을 전송하면 회원 가입을 할 수 있다.")
    @ParameterizedTest
    @CsvSource({
            "abcd@abc.com, person1, Abcd1234!, 홍길동, 01012345678, ROLE_BUYER",
            "abcd@abcd.com, person2, Abcd12345!, 고길동, 01012345679, ROLE_SELLER",
            "abcd@abcde.com, person3, Abcd123456!, 김길동, 01012345680, ROLE_ADMIN"
    })
    void signUpUser(String email, String username, String password, String fullName,
                    String phone, String role) {
        // given
        SignUpForm signUpForm = TestObjectFactory.enterUserForm
                (email, username, password, fullName, phone, List.of(role));

        // when
        User signedUpuser = authenticationService.signUpUser(signUpForm);
        User foundUser = userRepository.findByEmail(Email.of(email)).get();

        // then
        assertThat(foundUser.getEmail()).isEqualTo(Email.of(email));
        assertThat(foundUser.getUsername()).isEqualTo(Username.of(username));
        assertThat(foundUser.getRoles().get(0).toString()).isEqualTo(role);
        assertThat(foundUser.getPhone()).isEqualTo(Phone.of(phone));
        assertThat(foundUser.getCreatedAt()).isEqualTo(signedUpuser.getCreatedAt());
    }

    @DisplayName("올바른 회원 가입 양식을 전송하면 여러 권한을 가진 회원으로 가입할 수 있다.")
    @ParameterizedTest
    @CsvSource({
            "abcd@abc.com, person1, Abcd1234!, 홍길동, 01012345678, ROLE_BUYER, ROLE_SELLER",
            "abcd@abcd.com, person2, Abcd12345!, 고길동, 01012345679, ROLE_SELLER, ROLE_BUYER",
            "abcd@abcde.com, person3, Abcd123456!, 김길동, 01012345680, ROLE_BUYER, ROLE_ADMIN"
    })
    void signUpUserWithMultipleRoles(String email, String username, String password, String fullName,
                                     String phone, String role1, String role2) {
        // given
        SignUpForm signUpForm = TestObjectFactory.enterUserForm
                (email, username, password, fullName, phone, List.of(role1, role2));

        // when
        User signedUpuser = authenticationService.signUpUser(signUpForm);
        User foundUser = userRepository.findByEmail(Email.of(email)).get();

        // then
        assertThat(foundUser.getEmail()).isEqualTo(Email.of(email));
        assertThat(foundUser.getUsername()).isEqualTo(Username.of(username));
        assertThat(foundUser.getRoles().get(0).toString()).isEqualTo(role1);
        assertThat(foundUser.getRoles().get(1).toString()).isEqualTo(role2);
        assertThat(foundUser.getPhone()).isEqualTo(Phone.of(phone));
        assertThat(signedUpuser.getCreatedAt()).isEqualTo(foundUser.getCreatedAt());
    }

    @DisplayName("회원 가입 시 비밀번호를 암호화 해 저장할 수 있다.")
    @ParameterizedTest
    @CsvSource({
            "abcd@abc.com, person1, Abcd1234!, 홍길동, 01012345678, ROLE_BUYER, ROLE_SELLER",
            "abcd@abcd.com, person2, Abcd12345!, 고길동, 01012345679, ROLE_SELLER, ROLE_BUYER",
            "abcd@abcde.com, person3, Abcd123456!, 김길동, 01012345680, ROLE_BUYER, ROLE_ADMIN"
    })
    void signUpUserWithEncodingPassword(String email, String username, String password, String fullName,
                                        String phone, Role role1, Role role2) {
        // given
        SignUpForm signUpForm = TestObjectFactory.enterUserForm
                (email, username, password, fullName, phone, List.of(role1.toString(), role2.toString()));

        // when
        authenticationService.signUpUser(signUpForm);
        User user = userRepository.findByEmail(Email.of(email)).get();

        // then
        assertThat(user.getPassword()).isNotEqualTo(password);
        assertThat(user.getPassword().matchOriginalPassword(passwordEncoder, password)).isTrue();
    }

    @DisplayName("중복된 이메일을 전송하면 UserAlreadyExistsException이 발생한다.")
    @Test
    void signUpUserByDuplicateEmail() {
        // given
        SignUpForm signUpForm1 = TestObjectFactory.enterUserForm(EMAIL1, USERNAME1,
                PASSWORD1, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.toString()));

        authenticationService.signUpUser(signUpForm1);

        SignUpForm signUpForm2 = TestObjectFactory.enterUserForm(EMAIL1, USERNAME2,
                PASSWORD2, FULL_NAME2, PHONE2, List.of(ROLE_SELLER.toString()));

        // when, then
        assertThatThrownBy(() -> authenticationService.signUpUser(signUpForm2))
                .isInstanceOf(DuplicateUserException.class)
                .hasMessage(DUPLICATE_EMAIL_EXCEPTION + signUpForm2.getEmail());
    }

    @DisplayName("중복된 전화번호를 전송하면 UserAlreadyExistsException이 발생한다.")
    @Test
    void signUpUserByDuplicatePhone() {
        // given
        SignUpForm signUpForm1 = TestObjectFactory.enterUserForm(EMAIL1, USERNAME1,
                PASSWORD1, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.toString()));

        authenticationService.signUpUser(signUpForm1);

        SignUpForm signUpForm2 = TestObjectFactory.enterUserForm(EMAIL2, USERNAME2,
                PASSWORD2, FULL_NAME2, PHONE1, List.of(ROLE_SELLER.toString()));

        // when, then
        assertThatThrownBy(() -> authenticationService.signUpUser(signUpForm2))
                .isInstanceOf(DuplicateUserException.class)
                .hasMessage(DUPLICATE_PHONE_EXCEPTION + signUpForm2.getPhone());
    }

    @DisplayName("중복된 사용자 이름을 전송하면 UserAlreadyExistsException이 발생한다.")
    @Test
    void signUpUserByDuplicateUsername() {
        // given
        SignUpForm signUpForm1 = TestObjectFactory.enterUserForm(EMAIL1, USERNAME1,
                PASSWORD1, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.toString()));

        authenticationService.signUpUser(signUpForm1);

        SignUpForm signUpForm2 = TestObjectFactory.enterUserForm(EMAIL2, USERNAME1, PASSWORD2, FULL_NAME2,
                PHONE2, List.of(ROLE_SELLER.toString()));

        // when, then
        assertThatThrownBy(() -> authenticationService.signUpUser(signUpForm2))
                .isInstanceOf(DuplicateUsernameException.class)
                .hasMessage(DUPLICATE_USERNAME_EXCEPTION + signUpForm2.getUsername());
    }
}
