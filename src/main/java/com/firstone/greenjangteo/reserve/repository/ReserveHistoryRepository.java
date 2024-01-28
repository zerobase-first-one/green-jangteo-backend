package com.firstone.greenjangteo.reserve.repository;

import com.firstone.greenjangteo.reserve.model.entity.ReserveHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReserveHistoryRepository extends JpaRepository<ReserveHistory, Long> {
    Optional<ReserveHistory> findFirstByUserIdOrderByCreatedAtDesc(Long userId);
}
