package com.firstone.greenjangteo.coupon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstone.greenjangteo.coupon.dto.request.IssueCouponsRequestDto;
import com.firstone.greenjangteo.coupon.model.entity.Coupon;
import com.firstone.greenjangteo.coupon.model.entity.CouponGroup;
import com.firstone.greenjangteo.coupon.service.CouponGroupService;
import com.firstone.greenjangteo.coupon.service.CouponService;
import com.firstone.greenjangteo.coupon.testutil.CouponTestObjectFactory;
import com.firstone.greenjangteo.user.dto.request.UserIdRequestDto;
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

import java.time.LocalDate;
import java.util.List;

import static com.firstone.greenjangteo.coupon.testutil.CouponTestConstant.*;
import static com.firstone.greenjangteo.web.ApiConstant.ID_EXAMPLE;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(controllers = CouponController.class)
class CouponControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CouponService couponService;

    @MockBean
    private CouponGroupService couponGroupService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @DisplayName("올바른 쿠폰 요청 양식을 전송하면 대량의 쿠폰을 발행할 수 있다.")
    @Test
    @WithMockUser(username = ID_EXAMPLE, roles = {"ADMIN"})
    void issueCoupons() throws Exception {
        // given
        IssueCouponsRequestDto issueCouponsRequestDto
                = CouponTestObjectFactory.createIssueCouponsRequestDto(
                COUPON_NAME1, AMOUNT, DESCRIPTION, ISSUE_QUANTITY1,
                LocalDate.now().plusDays(1), EXPIRATION_PERIOD1
        );

        // when, then
        mockMvc.perform(post("/coupons")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(issueCouponsRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());
    }

    @DisplayName("회원의 쿠폰 목록을 조회할 수 있다.")
    @Test
    @WithMockUser(username = ID_EXAMPLE, roles = {"BUYER"})
    void getCoupons() throws Exception {
        // given
        UserIdRequestDto userIdRequestDto = new UserIdRequestDto(ID_EXAMPLE);
        CouponGroup couponGroup
                = CouponTestObjectFactory.createCouponGroup(
                COUPON_NAME1, AMOUNT, DESCRIPTION, ISSUE_QUANTITY1, LocalDate.now(), EXPIRATION_PERIOD1
        );

        List<Coupon> coupons = CouponTestObjectFactory.createCoupons(couponGroup);

        when(couponService.getCoupons(Long.parseLong(ID_EXAMPLE))).thenReturn(coupons);

        // when, then
        mockMvc.perform(get("/coupons")
                        .content(objectMapper.writeValueAsString(userIdRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
