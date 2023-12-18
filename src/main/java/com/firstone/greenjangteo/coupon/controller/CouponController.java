package com.firstone.greenjangteo.coupon.controller;

import com.firstone.greenjangteo.coupon.dto.IssueCouponsRequestDto;
import com.firstone.greenjangteo.coupon.service.CouponService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.firstone.greenjangteo.exception.message.AccessDeniedMessage.ADMIN_ONLY;
import static com.firstone.greenjangteo.user.model.Role.ROLE_ADMIN;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponController {
    private final CouponService couponService;

    private static final String ISSUE_COUPONS = "쿠폰 발행";
    private static final String ISSUE_COUPONS_DESCRIPTION = "쿠폰 발행 양식을 입력해 대량의 쿠폰을 발행할 수 있습니다.";
    private static final String ISSUE_COUPONS_FORM = "쿠폰 발행 양식";


    @ApiOperation(value = ISSUE_COUPONS, notes = ISSUE_COUPONS_DESCRIPTION)
    @PostMapping()
    public ResponseEntity<Void> issueCoupons
            (@Valid @RequestBody @ApiParam(value = ISSUE_COUPONS_FORM) IssueCouponsRequestDto issueCouponsRequestDto)
            throws JobExecutionException {
        checkAuthentication();

        couponService.createCoupons(issueCouponsRequestDto);

        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    private void checkAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority(ROLE_ADMIN.name()))) {
            return;
        }

        throw new AccessDeniedException(ADMIN_ONLY);
    }
}
