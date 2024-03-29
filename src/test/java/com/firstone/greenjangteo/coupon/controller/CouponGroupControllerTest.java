package com.firstone.greenjangteo.coupon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstone.greenjangteo.coupon.model.entity.Coupon;
import com.firstone.greenjangteo.coupon.model.entity.CouponGroup;
import com.firstone.greenjangteo.coupon.service.CouponGroupService;
import com.firstone.greenjangteo.coupon.testutil.CouponTestObjectFactory;
import com.firstone.greenjangteo.user.security.CustomAuthenticationEntryPoint;
import com.firstone.greenjangteo.user.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static com.firstone.greenjangteo.coupon.testutil.CouponTestConstant.*;
import static com.firstone.greenjangteo.utility.PagingConstant.*;
import static com.firstone.greenjangteo.web.ApiConstant.ID_EXAMPLE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(controllers = CouponGroupController.class)
public class CouponGroupControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CouponGroupService couponGroupService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @DisplayName("쿠폰 그룹 목록을 조회할 수 있다.")
    @Test
    @WithMockUser(username = ID_EXAMPLE, roles = {"ADMIN"})
    void getCouponGroups() throws Exception {
        // given
        CouponGroup couponGroup1
                = CouponTestObjectFactory.createCouponGroup(
                COUPON_NAME1, AMOUNT, DESCRIPTION, ISSUE_QUANTITY1, LocalDate.now(), EXPIRATION_PERIOD1
        );
        CouponGroup couponGroup2
                = CouponTestObjectFactory.createCouponGroup(
                COUPON_NAME1, AMOUNT, DESCRIPTION, ISSUE_QUANTITY1, LocalDate.now(), EXPIRATION_PERIOD1
        );

        List<CouponGroup> couponGroups = List.of(couponGroup1, couponGroup2);

        when(couponGroupService.getCouponGroups()).thenReturn(couponGroups);

        // when, then
        mockMvc.perform(get("/coupon-groups"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("쿠폰 그룹 ID를 입력해 쿠폰을 페이징 처리한 쿠폰 그룹을 조회할 수 있다.")
    @Test
    @WithMockUser(username = ID_EXAMPLE, roles = {"ADMIN"})
    void getCouponGroup() throws Exception {
        // given
        CouponGroup couponGroup
                = CouponTestObjectFactory.createCouponGroup(
                COUPON_NAME1, AMOUNT, DESCRIPTION, ISSUE_QUANTITY1, LocalDate.now(), EXPIRATION_PERIOD1
        );

        List<Coupon> coupons = CouponTestObjectFactory.createCoupons(couponGroup);

        Pageable pageable = PageRequest.of(0, 20);
        Page<Coupon> couponPage = new PageImpl<>(coupons, pageable, coupons.size());

        when(couponGroupService.getCouponGroup(any(Pageable.class), eq(1L))).thenReturn(couponPage);

        // when, then
        mockMvc.perform(get("/coupon-groups/{couponGroupId}", ID_EXAMPLE)
                        .param("paged", TRUE)
                        .param("page", ZERO)
                        .param("size", TWENTY)
                        .param("sort", ORDER_BY_ID_ASCENDING))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("쿠폰 그룹 ID를 입력해 쿠폰 그룹을 삭제할 수 있다.")
    @Test
    @WithMockUser(username = ID_EXAMPLE, roles = {"ADMIN"})
    void deleteCouponGroup() throws Exception {
        // given
        doNothing().when(couponGroupService).deleteCouponGroup(Long.parseLong(ID_EXAMPLE));

        // when, then
        mockMvc.perform(delete("/coupon-groups/{couponGroupId}", ID_EXAMPLE)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}
