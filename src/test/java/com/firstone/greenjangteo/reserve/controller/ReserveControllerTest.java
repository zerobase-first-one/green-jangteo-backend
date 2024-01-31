package com.firstone.greenjangteo.reserve.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstone.greenjangteo.reserve.dto.request.AddReserveRequestDto;
import com.firstone.greenjangteo.reserve.dto.request.UseReserveRequestDto;
import com.firstone.greenjangteo.reserve.model.CurrentReserve;
import com.firstone.greenjangteo.reserve.model.entity.ReserveHistory;
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

import java.util.List;

import static com.firstone.greenjangteo.order.testutil.OrderTestConstant.BUYER_ID;
import static com.firstone.greenjangteo.reserve.testutil.ReserveTestConstant.RESERVE1;
import static com.firstone.greenjangteo.reserve.testutil.ReserveTestConstant.RESERVE2;
import static com.firstone.greenjangteo.web.ApiConstant.ID_EXAMPLE;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

    @DisplayName("회원 ID와 차감할 적립금을 전송해 적립금을 차감할 수 있다.")
    @Test
    @WithMockUser(username = ID_EXAMPLE, roles = {"ADMIN"})
    void reduceReserve() throws Exception {
        // given
        UseReserveRequestDto useReserveRequestDto
                = ReserveTestObjectFactory.createUseReserveRequestDto(ID_EXAMPLE, RESERVE1);

        doNothing().when(reserveService).reduceReserve(useReserveRequestDto);

        // when, then
        mockMvc.perform(post("/reserves/reduce")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(useReserveRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @DisplayName("회원 ID를 통해 적립금 내역을 조회할 수 있다.")
    @Test
    @WithMockUser(username = BUYER_ID, roles = {"BUYER"})
    void getReserveHistories() throws Exception {
        // given
        Long userId = Long.parseLong(BUYER_ID);

        ReserveHistory reserveHistory1
                = ReserveTestObjectFactory.createReserveHistory(userId, RESERVE1, new CurrentReserve(RESERVE2));
        ReserveHistory reserveHistory2 = ReserveTestObjectFactory
                .createReserveHistory(userId, RESERVE1, new CurrentReserve(RESERVE1 + RESERVE2));

        List<ReserveHistory> reserveHistories = List.of(reserveHistory1, reserveHistory2);

        when(reserveService.getReserveHistories(userId)).thenReturn(reserveHistories);

        // when, then
        mockMvc.perform(get("/reserves")
                        .with(csrf())
                        .queryParam("userId", BUYER_ID))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("현재 적립금을 조회할 수 있다.")
    @Test
    @WithMockUser
    void getCurrentResolve() throws Exception {
        // given
        Long userId = Long.parseLong(BUYER_ID);

        ReserveHistory reserveHistory
                = ReserveTestObjectFactory.createReserveHistory(userId, RESERVE1, new CurrentReserve(RESERVE2));

        when(reserveService.getCurrentReserve(userId)).thenReturn(reserveHistory);

        // when, then
        mockMvc.perform(get("/reserves/current")
                        .with(csrf())
                        .queryParam("userId", BUYER_ID))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
