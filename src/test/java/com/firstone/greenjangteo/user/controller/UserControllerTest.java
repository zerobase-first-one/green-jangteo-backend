package com.firstone.greenjangteo.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstone.greenjangteo.user.dto.AddressDto;
import com.firstone.greenjangteo.user.model.entity.User;
import com.firstone.greenjangteo.user.security.CustomAuthenticationEntryPoint;
import com.firstone.greenjangteo.user.security.JwtTokenProvider;
import com.firstone.greenjangteo.user.service.UserService;
import com.firstone.greenjangteo.user.testutil.UserTestObjectFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.firstone.greenjangteo.user.model.Role.ROLE_BUYER;
import static com.firstone.greenjangteo.user.testutil.UserTestConstant.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @DisplayName("회원 ID를 입력하면 회원 개인정보를 조회할 수 있다.")
    @Test
    @WithMockUser
    void getUserDetails() throws Exception {
        // given
        User user = UserTestObjectFactory.createUser(1L, EMAIL1, USERNAME1, PASSWORD1, passwordEncoder,
                FULL_NAME1, PHONE1, List.of(ROLE_BUYER.toString()));

        when(userService.getUser(user.getId())).thenReturn(user);

        // when, then
        mockMvc.perform(get("/users/{userId}/profile", user.getId()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("회원 ID를 입력하면 회원 정보를 조회할 수 있다.")
    @Test
    @WithMockUser
    void getUser() throws Exception {
        // given
        User user = UserTestObjectFactory.createUser(1L, EMAIL1, USERNAME1, PASSWORD1, passwordEncoder,
                FULL_NAME1, PHONE1, List.of(ROLE_BUYER.toString()));

        when(userService.getUser(user.getId())).thenReturn(user);

        // when, then
        mockMvc.perform(get("/users/{userId}", user.getId()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("변경할 주소를 입력해 주소를 변경할 수 있다.")
    @Test
    @WithMockUser
    void updateAddress() throws Exception {
        // given
        User user = UserTestObjectFactory.createUser(1L, EMAIL1, USERNAME1, PASSWORD1, passwordEncoder,
                FULL_NAME1, PHONE1, List.of(ROLE_BUYER.toString()));

        AddressDto addressDto = AddressDto.builder()
                .city(CITY1)
                .street(STREET1)
                .zipcode(ZIPCODE1)
                .detailedAddress(DETAILED_ADDRESS1)
                .build();

        // when, then
        mockMvc.perform(patch("/users/{userId}/address", user.getId())
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(addressDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}