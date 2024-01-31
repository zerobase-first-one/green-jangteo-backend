package com.firstone.greenjangteo.user.domain.token.repository;

import com.firstone.greenjangteo.user.domain.token.model.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
}
