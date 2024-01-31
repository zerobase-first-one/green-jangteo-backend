package com.firstone.greenjangteo.user.domain.token.testutil;

import com.firstone.greenjangteo.user.domain.token.model.entity.RefreshToken;

import java.util.List;

public class TokenTestObjectFactory {
    public static RefreshToken createRefreshToken(Long userId, List<String> roles, String token) {
        return RefreshToken.from(userId, roles, token);
    }
}
