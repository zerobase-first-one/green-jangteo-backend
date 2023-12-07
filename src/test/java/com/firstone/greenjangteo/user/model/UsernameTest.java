package com.firstone.greenjangteo.user.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static com.firstone.greenjangteo.user.excpeption.message.BlankExceptionMessage.USERNAME_NO_VALUE_EXCEPTION;
import static com.firstone.greenjangteo.user.excpeption.message.InvalidExceptionMessage.INVALID_USERNAME_EXCEPTION;
import static com.firstone.greenjangteo.user.testutil.TestConstant.USERNAME1;
import static com.firstone.greenjangteo.user.testutil.TestConstant.USERNAME2;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class UsernameTest {
    @DisplayName("동일한 사용자 이름을 전송하면 동등한 Username 인스턴스를 생성한다.")
    @Test
    void ofSameValue() {
        // given, when
        Username username1 = Username.of(USERNAME1);
        Username username2 = Username.of(USERNAME1);

        // then
        assertThat(username1).isEqualTo(username2);
        assertThat(username1.hashCode()).isEqualTo(username2.hashCode());
    }

    @DisplayName("다른 사용자 이름을 전송하면 동등하지 않은 Username 인스턴스를 생성한다.")
    @Test
    void ofDifferentValue() {
        // given, when
        Username username1 = Username.of(USERNAME1);
        Username username2 = Username.of(USERNAME2);

        // then
        assertThat(username1).isNotEqualTo(username2);
        assertThat(username1.hashCode()).isNotEqualTo(username2.hashCode());
    }

    @DisplayName("사용자 이름을 전송하지 않으면 IllegalArgumentException이 발생한다.")
    @Test
    void ofBlankValue() {
        // given
        String username1 = null;
        String username2 = "";
        String username3 = " ";

        // when, then
        assertThatThrownBy(() -> Username.of(username1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(USERNAME_NO_VALUE_EXCEPTION);

        assertThatThrownBy(() -> Username.of(username2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(USERNAME_NO_VALUE_EXCEPTION);

        assertThatThrownBy(() -> Username.of(username3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(USERNAME_NO_VALUE_EXCEPTION);
    }

    @DisplayName("유효하지 않은 사용자 이름을 전송하면 IllegalArgumentException이 발생한다.")
    @ParameterizedTest
    @CsvSource({"ab c", "abc!", "abCde"})
    void ofInvalidValue(String username) {
        // given, when, then
        assertThatThrownBy(() -> Username.of(username))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(INVALID_USERNAME_EXCEPTION);
    }
}