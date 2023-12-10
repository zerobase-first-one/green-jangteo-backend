package com.firstone.greenjangteo.user.domain.store.testutil;

import com.firstone.greenjangteo.product.domain.model.Product;
import com.firstone.greenjangteo.user.domain.store.dto.StoreRequestDto;
import com.firstone.greenjangteo.user.domain.store.model.entity.Store;

public class TestObjectFactory {
    public static Store createStore(Long sellerId, String storeName, String description, String imageUrl) {
        Store store = Store.of(sellerId, storeName);

        StoreRequestDto storeRequestDto = new StoreRequestDto(storeName, description, imageUrl);
        store.update(storeRequestDto);

        return store;
    }

    public static Product createProduct(Store store, String productName, int price, int inventory) {
        return Product.builder()
                .store(store)
                .name(productName)
                .price(price)
                .inventory(inventory)
                .salesRate(0)
                .build();
    }
}
