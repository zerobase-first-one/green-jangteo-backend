package com.firstone.greenjangteo.reserve.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstone.greenjangteo.reserve.dto.request.AddReserveRequestDto;
import com.firstone.greenjangteo.reserve.service.ReserveService;
import com.firstone.greenjangteo.reserve.testutil.ReserveTestObjectFactory;
import com.firstone.greenjangteo.user.security.CustomAuthenticationEntryPoint;
import com.firstone.greenjangteo.user.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static com.firstone.greenjangteo.reserve.testutil.ReserveTestConstant.RESERVE1;
import static com.firstone.greenjangteo.web.ApiConstant.ID_EXAMPLE;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(controllers = ReserveController.class)
class ReserveControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReserveService reserveService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @DisplayName("회원 ID와 추가할 적립금을 전송해 적립금을 추가할 수 있다.")
    @Test
    @WithMockUser(username = ID_EXAMPLE, roles = {"ADMIN"})
    void addReserve() throws Exception {
        // given
        AddReserveRequestDto addReserveRequestDto
                = ReserveTestObjectFactory.createAddReserveRequestDto(ID_EXAMPLE, RESERVE1);

        doNothing().when(reserveService).addReserve(addReserveRequestDto);

        // when, then
        mockMvc.perform(post("/reserves/add")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(addReserveRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
