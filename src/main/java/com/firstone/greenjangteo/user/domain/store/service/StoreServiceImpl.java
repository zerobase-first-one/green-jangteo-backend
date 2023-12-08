package com.firstone.greenjangteo.user.domain.store.service;

import com.firstone.greenjangteo.user.domain.store.dto.StoreDto;
import com.firstone.greenjangteo.user.domain.store.exception.general.DuplicateStoreNameException;
import com.firstone.greenjangteo.user.domain.store.model.StoreName;
import com.firstone.greenjangteo.user.domain.store.model.entity.Store;
import com.firstone.greenjangteo.user.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

import static com.firstone.greenjangteo.user.domain.store.exception.ExceptionMessage.DUPLICATE_STORE_NAME_EXCEPTION;
import static com.firstone.greenjangteo.user.domain.store.exception.ExceptionMessage.STORE_NOT_FOUND_EXCEPTION;
import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;

@Service
@RequiredArgsConstructor
@Transactional(isolation = READ_COMMITTED, timeout = 10)
public class StoreServiceImpl implements StoreService {
    private final StoreRepository storeRepository;

    @Override
    public void createStore(Long userId, String storeName) {
        validateNotDuplicateStoreName(storeName);

        Store store = Store.of(userId, storeName);
        storeRepository.save(store);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED, readOnly = true, timeout = 10)
    public Store getStore(Long userId) {
        return storeRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(STORE_NOT_FOUND_EXCEPTION + userId));
    }

    @Override
    public void updateStore(Long userId, StoreDto storeDto) {
        Store store = getStore(userId);
        store.update(storeDto);
    }

    @Override
    public void deleteStore(long id) {
        if (storeRepository.existsById(id)) {
            storeRepository.deleteById(id);
            return;
        }

        throw new EntityNotFoundException(STORE_NOT_FOUND_EXCEPTION + id);
    }

    private void validateNotDuplicateStoreName(String storeName) {
        if (storeRepository.existsByStoreName(StoreName.of(storeName))) {
            throw new DuplicateStoreNameException(DUPLICATE_STORE_NAME_EXCEPTION + storeName);
        }
    }
}
