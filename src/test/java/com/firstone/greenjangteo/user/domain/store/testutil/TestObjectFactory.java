package com.firstone.greenjangteo.user.domain.store.testutil;

import com.firstone.greenjangteo.user.domain.store.dto.StoreDto;
import com.firstone.greenjangteo.user.domain.store.service.model.entity.Store;

public class TestObjectFactory {
    public static Store createStore(Long sellerId, String storeName, String description, String imageUrl) {
        Store store = Store.of(sellerId, storeName);

        StoreDto storeDto = StoreDto.of(storeName, description, imageUrl);
        store.update(storeDto);

        return store;
    }
}
