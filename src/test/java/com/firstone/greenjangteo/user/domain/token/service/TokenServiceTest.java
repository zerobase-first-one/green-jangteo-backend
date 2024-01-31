package com.firstone.greenjangteo.user.domain.token.service;

import com.firstone.greenjangteo.user.domain.token.repository.RefreshTokenRepository;
import com.firstone.greenjangteo.user.model.entity.User;
import com.firstone.greenjangteo.user.testutil.UserTestObjectFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.firstone.greenjangteo.user.model.Role.ROLE_BUYER;
import static com.firstone.greenjangteo.user.testutil.UserTestConstant.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class TokenServiceTest {
    @Autowired
    private TokenService tokenService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @DisplayName("엑세스 토큰을 발급할 수 있다.")
    @Test
    void issueAccessToken() {
        // given
        User user = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.toString())
        );

        // when
        String accessToken = tokenService.issueAccessToken(user);

        // then
        assertThat(accessToken).isNotBlank();
    }

    @DisplayName("리프레시 토큰을 발급하고 저장할 수 있다.")
    @Test
    void refreshAccessToken() {
        // given
        User user = UserTestObjectFactory.createUser(
                1L, EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.toString())
        );

        // when
        String createdRefreshToken = tokenService.issueRefreshToken(user);
        String foundRefreshToken = refreshTokenRepository.findAll().get(0).getTokenValue();

        // then
        assertThat(createdRefreshToken).isNotBlank();
        assertThat(foundRefreshToken).isEqualTo(createdRefreshToken);
    }
}
