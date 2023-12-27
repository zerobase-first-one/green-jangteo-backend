package com.firstone.greenjangteo.coupon.controller;

import com.firstone.greenjangteo.coupon.dto.request.IssueCouponsRequestDto;
import com.firstone.greenjangteo.coupon.dto.response.CouponResponseDto;
import com.firstone.greenjangteo.coupon.model.CouponAndGroupEntityToDtoMapper;
import com.firstone.greenjangteo.coupon.model.entity.Coupon;
import com.firstone.greenjangteo.coupon.service.CouponService;
import com.firstone.greenjangteo.user.dto.request.UserIdRequestDto;
import com.firstone.greenjangteo.utility.InputFormatValidator;
import com.firstone.greenjangteo.utility.RoleValidator;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.firstone.greenjangteo.web.ApiConstant.ID_EXAMPLE;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponController {
    private final CouponService couponService;

    private static final String ISSUE_COUPONS = "쿠폰 발행";
    private static final String ISSUE_COUPONS_DESCRIPTION
            = "쿠폰 발행 양식을 입력해 대량의 쿠폰을 최초 발행하거나 추가 발행할 수 있습니다.\n" +
            "issueQuantity에 -1을 입력하면 발행일에 모든 회원에게 자동 지급됩니다.";
    private static final String ISSUE_COUPONS_FORM = "쿠폰 발행 양식";

    private static final String GET_COUPONS = "회원 쿠폰 목록 조회;";
    private static final String GET_COUPONS_DESCRIPTION
            = "회원 ID를 입력해 쿠폰 목록을 조회할 수 있습니다.";
    private static final String GET_COUPONS_FORM = "쿠폰 목록 조회 양식";

    private static final String COUPON_ID = "쿠폰 ID";
    private static final String DELETE_COUPON = "쿠폰 삭제";
    private static final String DELETE_COUPON_FROM_ID_DESCRIPTION = "쿠폰 ID를 입력해 쿠폰 그룹을 삭제할 수 있습니다.";

    @ApiOperation(value = ISSUE_COUPONS, notes = ISSUE_COUPONS_DESCRIPTION)
    @PostMapping()
    public ResponseEntity<Void> issueCoupons
            (@Valid @RequestBody @ApiParam(value = ISSUE_COUPONS_FORM) IssueCouponsRequestDto issueCouponsRequestDto)
            throws JobExecutionException {
        RoleValidator.checkAdminAuthentication();

        couponService.createCoupons(issueCouponsRequestDto);

        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @ApiOperation(value = GET_COUPONS, notes = GET_COUPONS_DESCRIPTION)
    @GetMapping()
    public ResponseEntity<List<CouponResponseDto>> getCoupons
            (@Valid @RequestBody @ApiParam(value = GET_COUPONS_FORM) UserIdRequestDto userIdRequestDto)
            throws JobExecutionException {
        String userId = userIdRequestDto.getUserId();

        RoleValidator.checkAdminOrPrincipalAuthentication(userId);

        List<Coupon> coupons = couponService.getCoupons(Long.parseLong(userId));

        return ResponseEntity.status(HttpStatus.OK)
                .body(CouponAndGroupEntityToDtoMapper.toCouponResponseDtosForPrincipal(coupons));
    }

    @ApiOperation(value = DELETE_COUPON, notes = DELETE_COUPON_FROM_ID_DESCRIPTION)
    @DeleteMapping("/{couponId}")
    public ResponseEntity<Void> deleteCoupon
            (@PathVariable("couponId")
             @ApiParam(value = COUPON_ID, example = ID_EXAMPLE) String couponId) {
        InputFormatValidator.validateId(couponId);
        RoleValidator.checkAdminAuthentication();

        couponService.deleteCoupon(Long.parseLong(couponId));

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
