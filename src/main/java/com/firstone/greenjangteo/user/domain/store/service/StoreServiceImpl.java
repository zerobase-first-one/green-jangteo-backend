package com.firstone.greenjangteo.user.domain.store.service;

import com.firstone.greenjangteo.product.domain.model.Product;
import com.firstone.greenjangteo.product.repository.CategoryRepository;
import com.firstone.greenjangteo.user.domain.store.dto.StoreRequestDto;
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
    private final CategoryRepository categoryRepository;

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
    public void updateStore(Long userId, StoreRequestDto storeRequestDto) {
        Store store = getStore(userId);
        store.update(storeRequestDto);
    }

    @Override
    public void deleteStore(Long userId) {
        if (storeRepository.existsById(userId)) {
            deleteCategories(userId);
            storeRepository.deleteById(userId);
            return;
        }

        throw new EntityNotFoundException(STORE_NOT_FOUND_EXCEPTION + userId);
    }

    private void validateNotDuplicateStoreName(String storeName) {
        if (storeRepository.existsByStoreName(StoreName.of(storeName))) {
            throw new DuplicateStoreNameException(DUPLICATE_STORE_NAME_EXCEPTION + storeName);
        }
    }

    private void deleteCategories(Long userId) {
        getStore(userId).getProducts().stream()
                .map(Product::getId)
                .forEach(categoryRepository::deleteAllByProductId);
    }
}
