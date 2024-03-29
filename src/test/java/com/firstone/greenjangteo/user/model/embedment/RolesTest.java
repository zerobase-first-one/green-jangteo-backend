package com.firstone.greenjangteo.user.model.embedment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.List;

import static com.firstone.greenjangteo.user.excpeption.message.BlankExceptionMessage.ROLE_NO_VALUE_EXCEPTION;
import static com.firstone.greenjangteo.user.excpeption.message.InvalidExceptionMessage.INVALID_ROLE_EXCEPTION;
import static com.firstone.greenjangteo.user.model.Role.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class RolesTest {
    @DisplayName("유효한 값을 전송하면 여러 회원 분류를 가진 일급 컬렉션을 생성할 수 있다.")
    @Test
    void from() {
        // given
        String admin = ROLE_ADMIN.toString();
        String seller = ROLE_SELLER.toString();
        String buyer = ROLE_BUYER.toString();

        // when
        Roles roles1 = Roles.from(List.of(buyer));
        Roles roles2 = Roles.from(List.of(admin, seller));
        Roles roles3 = Roles.from(List.of(admin, seller, buyer));

        // then
        assertThat(roles1.get(0)).isEqualTo(ROLE_BUYER);

        assertThat(roles2.get(0)).isEqualTo(ROLE_ADMIN);
        assertThat(roles2.get(1)).isEqualTo(ROLE_SELLER);

        assertThat(roles3.get(0)).isEqualTo(ROLE_ADMIN);
        assertThat(roles3.get(1)).isEqualTo(ROLE_SELLER);
        assertThat(roles3.get(2)).isEqualTo(ROLE_BUYER);
    }

    @DisplayName("동일한 회원 분류를 전송하면 동등한 Roles 인스턴스를 생성한다.")
    @Test
    void fromSameValue() {
        // given, when
        Roles roles1 = Roles.from(List.of(ROLE_ADMIN.toString(), ROLE_BUYER.toString()));
        Roles roles2 = Roles.from(List.of(ROLE_ADMIN.toString(), ROLE_BUYER.toString()));

        // then
        assertThat(roles1).isEqualTo(roles2);
        assertThat(roles1.hashCode()).isEqualTo(roles2.hashCode());
    }

    @DisplayName("다른 회원 분류를 전송하면 동등하지 않은 Roles 인스턴스를 생성한다.")
    @Test
    void fromDifferentValue() {
        // given, when
        Roles roles1 = Roles.from(List.of(ROLE_ADMIN.toString(), ROLE_BUYER.toString()));
        Roles roles2 = Roles.from(List.of(ROLE_ADMIN.toString(), ROLE_SELLER.toString()));

        // then
        assertThat(roles1).isNotEqualTo(roles2);
        assertThat(roles1.hashCode()).isNotEqualTo(roles2.hashCode());
    }

    @DisplayName("회원 분류를 전송하지 않으면 IllegalArgumentException이 발생한다.")
    @Test
    void fromBlankValue() {
        // given, when, then
        assertThatThrownBy(() -> Roles.from(List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ROLE_NO_VALUE_EXCEPTION);

        assertThatThrownBy(() -> Roles.from(new ArrayList<String>()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ROLE_NO_VALUE_EXCEPTION);
    }

    @DisplayName("유효하지 않은 회원 분류를 전송하면 IllegalArgumentException이 발생한다.")
    @ParameterizedTest
    @CsvSource({"ROLE_ADMI", "ROLEBUYER", "SELLER", "관리자", "판매자", "구매자"})
    void fromInvalidValue(String role) {
        // given, when, then
        assertThatThrownBy(() -> Roles.from(List.of(role)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(INVALID_ROLE_EXCEPTION + role);
    }

    @DisplayName("사용자가 판매자의 권한을 갖고 있는지 여부를 확인한다.")
    @Test
    void checkContainSeller() {
        // given
        String admin = ROLE_ADMIN.toString();
        String seller = ROLE_SELLER.toString();
        String buyer = ROLE_BUYER.toString();

        Roles roles1 = Roles.from(List.of(seller, buyer));
        Roles roles2 = Roles.from(List.of(admin, buyer));

        // when, then
        assertThat(roles1.containSeller()).isTrue();
        assertThat(roles2.containSeller()).isFalse();
    }

    @DisplayName("사용자가 구매자의 권한을 갖고 있는지 여부를 확인한다.")
    @Test
    void checkContainBuyer() {
        // given
        String admin = ROLE_ADMIN.toString();
        String seller = ROLE_SELLER.toString();
        String buyer = ROLE_BUYER.toString();

        Roles roles1 = Roles.from(List.of(seller, buyer));
        Roles roles2 = Roles.from(List.of(admin, seller));

        // when, then
        assertThat(roles1.containBuyer()).isTrue();
        assertThat(roles2.containBuyer()).isFalse();
    }
}
