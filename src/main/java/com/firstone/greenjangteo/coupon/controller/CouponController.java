package com.firstone.greenjangteo.coupon.controller;

import com.firstone.greenjangteo.coupon.dto.request.IssueCouponsRequestDto;
import com.firstone.greenjangteo.coupon.dto.response.CouponGroupResponseDto;
import com.firstone.greenjangteo.coupon.model.CouponGroupEntityToDtoMapper;
import com.firstone.greenjangteo.coupon.model.entity.Coupon;
import com.firstone.greenjangteo.coupon.service.CouponService;
import com.firstone.greenjangteo.utility.InputFormatValidator;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.firstone.greenjangteo.exception.message.AccessDeniedMessage.ADMIN_ONLY;
import static com.firstone.greenjangteo.user.model.Role.ROLE_ADMIN;
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

    private static final String COUPON_GROUP_ID = "쿠폰 그룹 ID";
    private static final String GET_COUPON_GROUP = "쿠폰 그룹 조회";
    private static final String GET_COUPON_GROUP_DESCRIPTION
            = "쿠폰 그룹 ID를 입력해 쿠폰 그룹을 조회할 수 있습니다.\n쿠폰 목록의 페이징 옵션을 선택할 수 있습니다.";
    private static final String GET_COUPON_GROUP_FORM = "쿠폰 그룹 조회 양식";


    @ApiOperation(value = ISSUE_COUPONS, notes = ISSUE_COUPONS_DESCRIPTION)
    @PostMapping()
    public ResponseEntity<Void> issueCoupons
            (@Valid @RequestBody @ApiParam(value = ISSUE_COUPONS_FORM) IssueCouponsRequestDto issueCouponsRequestDto)
            throws JobExecutionException {
        checkAuthentication();

        couponService.createCoupons(issueCouponsRequestDto);

        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @ApiOperation(value = GET_COUPON_GROUP, notes = GET_COUPON_GROUP_DESCRIPTION)
    @GetMapping("/{couponGroupId}")
    public ResponseEntity<CouponGroupResponseDto> getCouponGroup
            (@PathVariable("couponGroupId")
             @ApiParam(value = COUPON_GROUP_ID, example = ID_EXAMPLE) String couponGroupId,
             @RequestParam(defaultValue = "true")
             @ApiParam(value = "페이지네이션 사용 여부", example = "true") boolean paged,
             @RequestParam(defaultValue = "0")
             @ApiParam(value = "현재 페이지 번호", example = "0") int page,
             @RequestParam(defaultValue = "20")
             @ApiParam(value = "페이지 당 항목 수", example = "20") int size,
             @RequestParam(defaultValue = "id,asc")
             @ApiParam(value = "정렬 방식", example = "id,asc") String sort) {
        InputFormatValidator.validateId(couponGroupId);
        checkAuthentication();
        Pageable pageable = paged ? PageRequest.of(page, size, parseSortString(sort)) : Pageable.unpaged();

        Page<Coupon> couponPage = couponService.getCouponGroup(Long.parseLong(couponGroupId), pageable);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CouponGroupEntityToDtoMapper.toCouponGroupResponseDto(couponPage));
    }

    private void checkAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority(ROLE_ADMIN.name()))) {
            return;
        }

        throw new AccessDeniedException(ADMIN_ONLY);
    }

    private Sort parseSortString(String sort) {
        String[] parts = sort.split(",");
        return Sort.by(Sort.Direction.fromString(parts[1]), parts[0]);
    }
}
