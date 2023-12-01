package com.firstone.greenjangteo.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstone.greenjangteo.user.dto.UserResponseDto;
import com.firstone.greenjangteo.user.form.SignUpForm;
import com.firstone.greenjangteo.user.service.AuthenticationService;
import com.firstone.greenjangteo.user.testutil.TestObjectFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static com.firstone.greenjangteo.user.model.Role.ROLE_BUYER;
import static com.firstone.greenjangteo.user.testutil.TestConstant.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(controllers = AuthenticationController.class)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationService authenticationService;

    @DisplayName("사용자가 올바른 회원 가입 양식을 입력하면 회원 가입을 할 수 있다.")
    @Test
    @WithMockUser
    void signUpUser() throws Exception {
        // given
        SignUpForm signUpForm = TestObjectFactory.enterUserForm(
                EMAIL, USERNAME, PASSWORD, FULL_NAME,
                PHONE, List.of(ROLE_BUYER.toString()));

        UserResponseDto userResponseDto = UserResponseDto.of(1L, LocalDateTime.now());

        when(authenticationService.signUpUser(any(SignUpForm.class))).thenReturn(userResponseDto);

        // when, then
        mockMvc.perform(post("/users/signup")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(signUpForm))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }
}