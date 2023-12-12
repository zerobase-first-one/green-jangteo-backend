package com.firstone.greenjangteo.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstone.greenjangteo.user.dto.request.DeleteRequestDto;
import com.firstone.greenjangteo.user.dto.request.EmailRequestDto;
import com.firstone.greenjangteo.user.dto.request.PasswordUpdateRequestDto;
import com.firstone.greenjangteo.user.dto.request.PhoneRequestDto;
import com.firstone.greenjangteo.user.form.SignInForm;
import com.firstone.greenjangteo.user.form.SignUpForm;
import com.firstone.greenjangteo.user.model.entity.User;
import com.firstone.greenjangteo.user.security.CustomAuthenticationEntryPoint;
import com.firstone.greenjangteo.user.security.JwtTokenProvider;
import com.firstone.greenjangteo.user.service.AuthenticationService;
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
import static com.firstone.greenjangteo.user.model.Role.ROLE_SELLER;
import static com.firstone.greenjangteo.user.testutil.UserTestConstant.*;
import static com.firstone.greenjangteo.user.testutil.UserTestObjectFactory.enterUserForm;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @MockBean
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @DisplayName("사용자가 올바른 회원 가입 양식을 입력하면 회원 가입을 할 수 있다.")
    @Test
    @WithMockUser
    void signUpUser() throws Exception {
        // given
        SignUpForm signUpForm = enterUserForm(
                EMAIL1, USERNAME1, PASSWORD1, PASSWORD1, FULL_NAME1,
                PHONE1, java.util.List.of(ROLE_BUYER.toString()));

        User user = mock(User.class);

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
        SignUpForm signUpForm = UserTestObjectFactory.enterUserForm
                (EMAIL1, USERNAME1, PASSWORD1, PASSWORD1, FULL_NAME1,
                        PHONE1, List.of(ROLE_SELLER.toString()));

        User user = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_SELLER.toString())
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
        User user = UserTestObjectFactory.createUser(1L, EMAIL1, USERNAME1, PASSWORD1, passwordEncoder,
                FULL_NAME1, PHONE1, List.of(ROLE_BUYER.toString()));

        EmailRequestDto emailRequestDto = EmailRequestDto.builder()
                .password(PASSWORD1)
                .email(EMAIL1)
                .build();

        // when, then
        mockMvc.perform(patch("/users/{userId}/email", user.getId())
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(emailRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @DisplayName("비밀번호와 변경할 전화번호를 입력해 전화번호를 변경할 수 있다.")
    @Test
    @WithMockUser
    void updatePhone() throws Exception {
        // given
        User user = UserTestObjectFactory.createUser(1L, EMAIL1, USERNAME1, PASSWORD1, passwordEncoder,
                FULL_NAME1, PHONE1, List.of(ROLE_BUYER.toString()));

        PhoneRequestDto phoneRequestDto = PhoneRequestDto.builder()
                .password(PASSWORD1)
                .phone(PHONE1)
                .build();

        // when, then
        mockMvc.perform(patch("/users/{userId}/phone", user.getId())
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(phoneRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @DisplayName("현재 비밀번호와 변경할 비밀번호를 입력해 비밀번호를 변경할 수 있다.")
    @Test
    @WithMockUser
    void updatePassword() throws Exception {
        // given
        User user = UserTestObjectFactory.createUser(1L, EMAIL1, USERNAME1, PASSWORD1, passwordEncoder,
                FULL_NAME1, PHONE1, List.of(ROLE_BUYER.toString()));

        PasswordUpdateRequestDto passwordUpdateRequestDto = PasswordUpdateRequestDto.builder()
                .currentPassword(PASSWORD1)
                .passwordToChange(PASSWORD1)
                .build();

        // when, then
        mockMvc.perform(patch("/users/{userId}/password", user.getId())
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(passwordUpdateRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @DisplayName("비밀번호 인증을 통해 회원을 탈퇴할 수 있다.")
    @Test
    @WithMockUser
    void deleteUser() throws Exception {
        // given
        User user = UserTestObjectFactory.createUser(1L, EMAIL1, USERNAME1, PASSWORD1, passwordEncoder,
                FULL_NAME1, PHONE1, List.of(ROLE_BUYER.toString()));

        DeleteRequestDto deleteRequestDto = new DeleteRequestDto(PASSWORD1);

        // when, then
        mockMvc.perform(delete("/users/{userId}", user.getId())
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(deleteRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}