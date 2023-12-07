package com.firstone.greenjangteo.user.domain.store.service;

import com.firstone.greenjangteo.user.domain.store.dto.StoreDto;
import com.firstone.greenjangteo.user.domain.store.service.model.entity.Store;

public interface StoreService {
    void createStore(Long userId, String storeName);

    Store getStore(Long userId);

    void updateStore(Long userId, StoreDto storeDto);

    void deleteStore(long id);
}
