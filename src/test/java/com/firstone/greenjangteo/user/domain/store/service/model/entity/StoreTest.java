package com.firstone.greenjangteo.user.domain.store.service.model.entity;

import com.firstone.greenjangteo.user.domain.store.model.StoreName;
import com.firstone.greenjangteo.user.domain.store.model.entity.Store;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static com.firstone.greenjangteo.user.domain.store.testutil.TestConstant.STORE_NAME1;
import static com.firstone.greenjangteo.user.domain.store.testutil.TestConstant.STORE_NAME2;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class StoreTest {
    @DisplayName("올바른 값을 전송하면 가게 인스턴스를 생성할 수 있다.")
    @ParameterizedTest
    @CsvSource({"1, 친환경 스토어", "1, green jangteo", "350, 착한 가게"})
    void of(Long sellerId, String storeName) {
        // given, when
        Store store = Store.of(sellerId, storeName);

        // then
        assertThat(store.getSellerId()).isEqualTo(sellerId);
        assertThat(store.getStoreName()).isEqualTo(StoreName.of(storeName));
    }

    @DisplayName("동일한 내부 값들을 전송하면 동등한 Store 인스턴스를 생성한다.")
    @Test
    void ofSameValue() {
        // given, when
        Store store1 = Store.of(1L, STORE_NAME1);
        Store store2 = Store.of(1L, STORE_NAME1);

        // then
        assertThat(store1).isEqualTo(store2);
        assertThat(store1.hashCode()).isEqualTo(store2.hashCode());
    }

    @DisplayName("다른 내부 값들을 전송하면 동등하지 않은 Store 인스턴스를 생성한다.")
    @Test
    void ofDifferentValue() {
        // given, when
        Store store1 = Store.of(1L, STORE_NAME1);
        Store store2 = Store.of(1L, STORE_NAME2);
        Store store3 = Store.of(2L, STORE_NAME1);

        // then
        assertThat(store1).isNotEqualTo(store2);
        assertThat(store1.hashCode()).isNotEqualTo(store2.hashCode());

        assertThat(store1).isNotEqualTo(store3);
        assertThat(store1.hashCode()).isNotEqualTo(store3.hashCode());
    }
}