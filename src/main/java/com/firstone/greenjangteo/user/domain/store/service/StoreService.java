package com.firstone.greenjangteo.user.domain.store.service;

import com.firstone.greenjangteo.user.domain.store.dto.StoreRequestDto;
import com.firstone.greenjangteo.user.domain.store.model.entity.Store;

public interface StoreService {
    void createStore(Long userId, String storeName);

    Store getStore(Long userId);

    void updateStore(Long userId, StoreRequestDto storeRequestDto);

    void deleteStore(Long userId);
}
