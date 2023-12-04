package com.firstone.greenjangteo.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstone.greenjangteo.common.security.JwtTokenProvider;
import com.firstone.greenjangteo.user.dto.AddressDto;
import com.firstone.greenjangteo.user.dto.EmailRequestDto;
import com.firstone.greenjangteo.user.form.SignInForm;
import com.firstone.greenjangteo.user.form.SignUpForm;
import com.firstone.greenjangteo.user.model.entity.User;
import com.firstone.greenjangteo.user.service.AuthenticationService;
import com.firstone.greenjangteo.user.testutil.TestObjectFactory;
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
import static com.firstone.greenjangteo.user.model.Role.ROLE_SELLER;
import static com.firstone.greenjangteo.user.testutil.TestConstant.*;
import static com.firstone.greenjangteo.user.testutil.TestObjectFactory.enterUserForm;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @DisplayName("사용자가 올바른 회원 가입 양식을 입력하면 회원 가입을 할 수 있다.")
    @Test
    @WithMockUser
    void signUpUser() throws Exception {
        // given
        SignUpForm signUpForm = enterUserForm(
                EMAIL, USERNAME, PASSWORD, FULL_NAME,
                PHONE, List.of(ROLE_BUYER.toString()));

        User user = TestObjectFactory.createUser(
                EMAIL, USERNAME, PASSWORD, passwordEncoder, FULL_NAME, PHONE, List.of(ROLE_BUYER.toString())
        );

        when(authenticationService.signUpUser(any(SignUpForm.class))).thenReturn(user);

        // when, then
        mockMvc.perform(post("/users/signup")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(signUpForm))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @DisplayName("올바른 로그인 양식을 입력하면 로그인을 할 수 있다.")
    @Test
    @WithMockUser
    void signInUser() throws Exception {
        // given
        SignUpForm signUpForm = enterUserForm
                (EMAIL, USERNAME, PASSWORD, FULL_NAME,
                        PHONE, List.of(ROLE_SELLER.toString()));

        User user = TestObjectFactory.createUser(
                EMAIL, USERNAME, PASSWORD, passwordEncoder, FULL_NAME, PHONE, List.of(ROLE_SELLER.toString())
        );

        when(authenticationService.signInUser(any(SignInForm.class)))
                .thenReturn(user);

        // when, then
        mockMvc.perform(post("/users/login")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(signUpForm))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("비밀번호와 변경할 이메일 주소를 입력해 이메일 주소를 변경할 수 있다.")
    @Test
    @WithMockUser
    void updateEmail() throws Exception {
        // given
        User user = TestObjectFactory.createUser(1L, EMAIL, USERNAME, PASSWORD, passwordEncoder,
                FULL_NAME, PHONE, List.of(ROLE_BUYER.toString()));

        EmailRequestDto emailRequestDto = EmailRequestDto.builder()
                .password(PASSWORD)
                .email(EMAIL)
                .build();

        // when, then
        mockMvc.perform(patch("/users/{userId}/email", user.getId())
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(emailRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}
