package com.firstone.greenjangteo.user.domain.store.service;

import com.firstone.greenjangteo.product.domain.model.Product;
import com.firstone.greenjangteo.product.repository.ProductRepository;
import com.firstone.greenjangteo.user.domain.store.dto.StoreRequestDto;
import com.firstone.greenjangteo.user.domain.store.exception.general.DuplicateStoreNameException;
import com.firstone.greenjangteo.user.domain.store.model.StoreName;
import com.firstone.greenjangteo.user.domain.store.model.entity.Store;
import com.firstone.greenjangteo.user.domain.store.repository.StoreRepository;
import com.firstone.greenjangteo.user.domain.store.testutil.StoreTestObjectFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static com.firstone.greenjangteo.user.domain.store.exception.ExceptionMessage.DUPLICATE_STORE_NAME_EXCEPTION;
import static com.firstone.greenjangteo.user.domain.store.testutil.StoreTestConstant.*;
import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class StoreServiceTest {
    @Autowired
    private StoreService storeService;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EntityManager entityManager;

    @DisplayName("올바른 가게 이름을 전송하면 가게를 생성할 수 있다.")
    @ParameterizedTest
    @CsvSource({"친환경 스토어", "green jangteo", "착한 가게"})
    void createStore(String storeName) {
        // given, when
        storeService.createStore(1L, storeName);
        Store store = storeRepository.findById(1L).get();

        // then
        assertThat(store.getStoreName()).isEqualTo(StoreName.of(storeName));
    }

    @DisplayName("중복된 가게 이름으로 가게를 생성하려 하면 DuplicateStoreNameException이 발생한다.")
    @Test
    void createStoreWithInvalidValue() {
        // given
        Store createdStore = StoreTestObjectFactory.createStore(1L, STORE_NAME1, DESCRIPTION1, IMAGE_URL1);
        storeRepository.save(createdStore);

        // when, then
        assertThatThrownBy(() -> storeService.createStore(2L, STORE_NAME1))
                .isInstanceOf(DuplicateStoreNameException.class)
                .hasMessage(DUPLICATE_STORE_NAME_EXCEPTION + STORE_NAME1);
    }

    @DisplayName("자신의 가게를 조회할 수 있다.")
    @Test
    void getStore() {
        // given
        Store createdStore = StoreTestObjectFactory.createStore(1L, STORE_NAME1, DESCRIPTION1, IMAGE_URL1);
        Store savedStore = storeRepository.save(createdStore);

        Product product1 = StoreTestObjectFactory.createProduct(savedStore, PRODUCT_NAME1, PRICE1, INVENTORY1);
        Product product2 = StoreTestObjectFactory.createProduct(savedStore, PRODUCT_NAME2, PRICE2, INVENTORY2);

        productRepository.saveAll(List.of(product1, product2));

        entityManager.flush();
        entityManager.refresh(savedStore);

        // when
        Store foundStore = storeRepository.findById(createdStore.getSellerId()).get();

        // then
        assertThat(foundStore.getSellerId()).isEqualTo(createdStore.getSellerId());
        assertThat(foundStore.getStoreName()).isEqualTo(createdStore.getStoreName());
        assertThat(foundStore.getDescription()).isEqualTo(createdStore.getDescription());
        assertThat(foundStore.getImageUrl()).isEqualTo(createdStore.getImageUrl());
        assertThat(foundStore.getProducts()).hasSize(2)
                .extracting("name", "price", "inventory", "salesRate")
                .containsExactlyInAnyOrder(
                        tuple(product1.getName(), product1.getPrice(),
                                product1.getInventory(), product1.getSalesRate()),
                        tuple(product2.getName(), product2.getPrice(),
                                product2.getInventory(), product2.getSalesRate())
                );
    }

    @DisplayName("자신의 가게를 수정할 수 있다.")
    @Test
    void updateStore() {
        // given
        Store createdStore = StoreTestObjectFactory.createStore(1L, STORE_NAME1, DESCRIPTION1, IMAGE_URL1);
        storeRepository.save(createdStore);

        // when
        storeService.updateStore(1L, new StoreRequestDto(STORE_NAME2, DESCRIPTION2, IMAGE_URL2));
        Store foundStore = storeRepository.findById(createdStore.getSellerId()).get();

        // then
        assertThat(foundStore.getStoreName()).isEqualTo(StoreName.of(STORE_NAME2));
        assertThat(foundStore.getDescription()).isEqualTo(DESCRIPTION2);
        assertThat(foundStore.getImageUrl()).isEqualTo(IMAGE_URL2);
    }
}