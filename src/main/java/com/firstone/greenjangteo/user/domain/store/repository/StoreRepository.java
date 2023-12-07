package com.firstone.greenjangteo.user.domain.store.repository;

import com.firstone.greenjangteo.user.domain.store.service.model.StoreName;
import com.firstone.greenjangteo.user.domain.store.service.model.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
    boolean existsByStoreName(StoreName storeName);
}
