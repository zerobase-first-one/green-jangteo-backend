package com.firstone.greenjangteo.user.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static com.firstone.greenjangteo.user.excpeption.message.BlankExceptionMessage.FULL_NAME_NO_VALUE_EXCEPTION;
import static com.firstone.greenjangteo.user.excpeption.message.InvalidExceptionMessage.INVALID_FULL_NAME_EXCEPTION;
import static com.firstone.greenjangteo.user.testutil.TestConstant.FULL_NAME1;
import static com.firstone.greenjangteo.user.testutil.TestConstant.FULL_NAME2;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class FullNameTest {
    @DisplayName("동일한 성명을 전송하면 동등한 FullName 인스턴스를 생성한다.")
    @Test
    void ofSameValue() {
        // given, when
        FullName fullName1 = FullName.of(FULL_NAME1);
        FullName fullName2 = FullName.of(FULL_NAME1);

        // then
        assertThat(fullName1).isEqualTo(fullName2);
    }

    @DisplayName("다른 성명을 전송하면 동등하지 않은 FullName 인스턴스를 생성한다.")
    @Test
    void ofDifferentValue() {
        // given, when
        FullName fullName1 = FullName.of(FULL_NAME1);
        FullName fullName2 = FullName.of(FULL_NAME2);

        // then
        assertThat(fullName1).isNotEqualTo(fullName2);
    }

    @DisplayName("성명을 전송하지 않으면 IllegalArgumentException이 발생한다.")
    @Test
    void ofBlankValue() {
        // given
        String fullName1 = "";
        String fullName2 = null;

        // when, then
        assertThatThrownBy(() -> FullName.of(fullName1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(FULL_NAME_NO_VALUE_EXCEPTION);

        assertThatThrownBy(() -> FullName.of(fullName2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(FULL_NAME_NO_VALUE_EXCEPTION);
    }

    @DisplayName("유효하지 않은 성명을 전송하면 IllegalArgumentException이 발생한다.")
    @ParameterizedTest
    @CsvSource({"홍길 동", "홍길1동", "홍길동고길동", "james"})
    void ofInvalidValue(String fullName) {
        // given, when, then
        assertThatThrownBy(() -> FullName.of(fullName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(INVALID_FULL_NAME_EXCEPTION);
    }
}