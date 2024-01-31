package com.firstone.greenjangteo.user.domain.token.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstone.greenjangteo.user.domain.token.service.TokenService;
import com.firstone.greenjangteo.user.security.CustomAuthenticationEntryPoint;
import com.firstone.greenjangteo.user.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(controllers = TokenController.class)
class TokenControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TokenService tokenService;

    @MockBean
    JwtTokenProvider jwtTokenProvider;

    @MockBean
    CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @DisplayName("올바른 리프레시 토큰 값을 입력하면 새로운 엑세스 토큰을 발급할 수 있다.")
    @Test
    @WithMockUser
    void issueNewAccessToken() throws Exception {
        // given
        String REFRESH_TOKEN_VALUE = "refreshTokenExample";

        when(tokenService.issueNewAccessToken(REFRESH_TOKEN_VALUE)).thenReturn("newAccessTokenExample");

        // when, then
        mockMvc.perform(post("/token")
                        .queryParam("refreshToken", REFRESH_TOKEN_VALUE)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isCreated());
    }
}
