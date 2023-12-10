package com.firstone.greenjangteo.user.domain.store.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstone.greenjangteo.user.domain.store.dto.StoreRequestDto;
import com.firstone.greenjangteo.user.domain.store.model.entity.Store;
import com.firstone.greenjangteo.user.domain.store.service.StoreService;
import com.firstone.greenjangteo.user.domain.store.testutil.TestObjectFactory;
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

import static com.firstone.greenjangteo.user.domain.store.testutil.TestConstant.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(controllers = StoreController.class)
class StoreControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StoreService storeService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @DisplayName("판매자의 ID를 통해 자신의 가게 정보를 조회할 수 있다.")
    @WithMockUser
    @Test
    void getStore() throws Exception {
        // given
        Store store = TestObjectFactory.createStore(1L, STORE_NAME1, DESCRIPTION1, IMAGE_URL1);

        when(storeService.getStore(store.getSellerId())).thenReturn(store);

        // when, then
        mockMvc.perform(get("/stores/{userId}", store.getSellerId()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("수정할 값들을 입력해 가게 정보를 수정할 수 있다.")
    @WithMockUser
    @Test
    void updateStore() throws Exception {
        // given
        Store store = TestObjectFactory.createStore(1L, STORE_NAME1, DESCRIPTION1, IMAGE_URL1);

        StoreRequestDto storeRequestDto = new StoreRequestDto(STORE_NAME1, DESCRIPTION1, IMAGE_URL1);

        // when, then
        mockMvc.perform(put("/stores/{userId}", store.getSellerId())
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(storeRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}