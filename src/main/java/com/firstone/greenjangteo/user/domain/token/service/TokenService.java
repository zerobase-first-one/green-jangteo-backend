package com.firstone.greenjangteo.user.domain.token.service;

import com.firstone.greenjangteo.user.domain.token.model.entity.RefreshToken;
import com.firstone.greenjangteo.user.domain.token.repository.RefreshTokenRepository;
import com.firstone.greenjangteo.user.model.entity.User;
import com.firstone.greenjangteo.user.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public String issueAccessToken(User user) {
        String accessToken
                = jwtTokenProvider.generateAccessToken(String.valueOf(user.getId()), user.getRoles().toStrings());

        return accessToken;
    }

    public String issueRefreshToken(User user) {
        String refreshToken
                = jwtTokenProvider.generateRefreshToken(String.valueOf(user.getId()), user.getRoles().toStrings());
        refreshTokenRepository.save(RefreshToken.from(user.getId(), user.getRoles().toStrings(), refreshToken));

        return refreshToken;
    }
}
