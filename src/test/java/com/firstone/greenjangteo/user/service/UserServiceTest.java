package com.firstone.greenjangteo.user.service;

import com.firstone.greenjangteo.user.dto.AddressDto;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static com.firstone.greenjangteo.user.excpeption.message.NotFoundExceptionMessage.USER_ID_NOT_FOUND_EXCEPTION;
import static com.firstone.greenjangteo.user.model.Role.ROLE_BUYER;
import static com.firstone.greenjangteo.user.testutil.TestConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@TestPropertySource("classpath:application-test.properties")
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
        User user = TestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.toString())
        );

        userRepository.save(user);

        // when, then
        assertThatThrownBy(() -> userService.getUser(user.getId() + 1))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(USER_ID_NOT_FOUND_EXCEPTION + (user.getId() + 1));
    }

    @DisplayName("변경할 주소를 입력해 주소를 변경할 수 있다.")
    @Test
    void updateAddress() {
        // given
        User user = TestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.toString())
        );
        userRepository.save(user);

        AddressDto addressDto1 = AddressDto.builder()
                .city(CITY2)
                .street(STREET2)
                .zipcode(ZIPCODE2)
                .detailedAddress(DETAILED_ADDRESS2)
                .build();

        // when
        userService.updateAddress(user.getId(), addressDto1);
        AddressDto addressDto2 = user.getAddress().toDto();

        // then
        assertThat(addressDto2.getCity()).isEqualTo(addressDto1.getCity());
        assertThat(addressDto2.getStreet()).isEqualTo(addressDto1.getStreet());
        assertThat(addressDto2.getZipcode()).isEqualTo(addressDto1.getZipcode());
        assertThat(addressDto2.getDetailedAddress()).isEqualTo(addressDto1.getDetailedAddress());
    }

    @DisplayName("유효하지 않은 주소를 입력하면 주소를 변경할 수 없다.")
    @ParameterizedTest
    @CsvSource({
            "서울특별시임, 테헤란로, 12345, 서울특별시 강남구 테헤란로 2길",
            "서울, 테헤란 street, 12345, 서울특별시 강남구 테헤란로 2길",
            "서울, 테헤란로, 123456, 서울특별시 강남구 테헤란로 2길",
            "서울, 테헤란로, 12345, 서울특별시 강남구 테헤란로 2길!",
    })
    void updateAddressWithInvalidAddress(String city, String street, String zipcode, String detailedAddress) {
        // given
        User user = TestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.toString())
        );
        userRepository.save(user);

        AddressDto addressDto = AddressDto.builder()
                .city(city)
                .street(street)
                .zipcode(zipcode)
                .detailedAddress(detailedAddress)
                .build();

        // when, then
        assertThatThrownBy(() -> userService.updateAddress(user.getId(), addressDto))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
