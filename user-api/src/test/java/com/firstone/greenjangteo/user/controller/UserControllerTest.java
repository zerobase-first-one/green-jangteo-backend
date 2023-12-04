package com.firstone.greenjangteo.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstone.greenjangteo.user.model.entity.User;
import com.firstone.greenjangteo.user.service.UserService;
import com.firstone.greenjangteo.user.testutil.TestObjectFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.firstone.greenjangteo.user.model.Role.ROLE_BUYER;
import static com.firstone.greenjangteo.user.testutil.TestConstant.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserController userController;

    @MockBean
    private UserService userService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @DisplayName("회원 ID를 입력하면 회원 개인정보를 조회할 수 있다.")
    @Test
    @WithMockUser
    void getUserDetails() throws Exception {
        // given
        User user = TestObjectFactory.createUser(1L, EMAIL, USERNAME, PASSWORD, passwordEncoder,
                FULL_NAME, PHONE, List.of(ROLE_BUYER.toString()));

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
        User user = TestObjectFactory.createUser(1L, EMAIL, USERNAME, PASSWORD, passwordEncoder,
                FULL_NAME, PHONE, List.of(ROLE_BUYER.toString()));

        when(userService.getUser(user.getId())).thenReturn(user);

        // when, then
        mockMvc.perform(get("/users/{userId}", user.getId()))
                .andDo(print())
                .andExpect(status().isOk());
    }
}