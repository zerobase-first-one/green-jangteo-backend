package com.firstone.greenjangteo.user.domain.store.service.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static com.firstone.greenjangteo.user.domain.store.exception.ExceptionMessage.INVALID_STORE_NAME_EXCEPTION;
import static com.firstone.greenjangteo.user.domain.store.exception.ExceptionMessage.STORE_NAME_NO_VALUE_EXCEPTION;
import static com.firstone.greenjangteo.user.domain.store.testutil.TestConstant.STORE_NAME1;
import static com.firstone.greenjangteo.user.domain.store.testutil.TestConstant.STORE_NAME2;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class StoreNameTest {
    @DisplayName("동일한 가게 이름을 전송하면 동등한 StoreName 인스턴스를 생성한다.")
    @Test
    void ofSameValue() {
        // given, when
        StoreName storeName1 = StoreName.of(STORE_NAME1);
        StoreName storeName2 = StoreName.of(STORE_NAME1);

        // then
        assertThat(storeName1).isEqualTo(storeName2);
        assertThat(storeName1.hashCode()).isEqualTo(storeName2.hashCode());
    }

    @DisplayName("다른 가게 이름을 전송하면 동등하지 않은 StoreName 인스턴스를 생성한다.")
    @Test
    void ofDifferentValue() {
        // given, when
        StoreName storeName1 = StoreName.of(STORE_NAME1);
        StoreName storeName2 = StoreName.of(STORE_NAME2);

        // then
        assertThat(storeName1).isNotEqualTo(storeName2);
        assertThat(storeName1.hashCode()).isNotEqualTo(storeName2.hashCode());
    }

    @DisplayName("가게 이름을 전송하지 않으면 IllegalArgumentException이 발생한다.")
    @Test
    void ofBlankValue() {
        // given
        String storeName1 = null;
        String storeName2 = "";
        String storeName3 = " ";

        // when, then
        assertThatThrownBy(() -> StoreName.of(storeName1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(STORE_NAME_NO_VALUE_EXCEPTION);

        assertThatThrownBy(() -> StoreName.of(storeName2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(STORE_NAME_NO_VALUE_EXCEPTION);

        assertThatThrownBy(() -> StoreName.of(storeName3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(STORE_NAME_NO_VALUE_EXCEPTION);
    }

    @DisplayName("유효하지 않은 가게 이름을 전송하면 IllegalArgumentException이 발생한다.")
    @ParameterizedTest
    @CsvSource({"abcdefghijklmnopqrstu", "친환경 스토어!", "친환경 스토어1"})
    void ofInvalidValue(String storeName) {
        // given, when, then
        assertThatThrownBy(() -> StoreName.of(storeName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(INVALID_STORE_NAME_EXCEPTION);
    }
}