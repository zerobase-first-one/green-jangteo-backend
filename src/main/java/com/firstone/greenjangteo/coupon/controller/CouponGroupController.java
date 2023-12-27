package com.firstone.greenjangteo.coupon.controller;

import com.firstone.greenjangteo.coupon.dto.response.CouponGroupResponseDto;
import com.firstone.greenjangteo.coupon.model.CouponGroupEntityToDtoMapper;
import com.firstone.greenjangteo.coupon.model.entity.Coupon;
import com.firstone.greenjangteo.coupon.service.CouponGroupService;
import com.firstone.greenjangteo.coupon.utility.AdminRoleValidator;
import com.firstone.greenjangteo.utility.InputFormatValidator;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.firstone.greenjangteo.web.ApiConstant.ID_EXAMPLE;

@RestController
@RequestMapping("/coupon-groups")
@RequiredArgsConstructor
public class CouponGroupController {
    private final CouponGroupService couponGroupService;

    private static final String COUPON_GROUP_ID = "쿠폰 그룹 ID";
    private static final String GET_COUPON_GROUP = "쿠폰 그룹 조회";
    private static final String GET_COUPON_GROUP_FROM_ID_DESCRIPTION
            = "쿠폰 그룹 ID를 입력해 쿠폰 그룹을 조회할 수 있습니다.\n쿠폰 목록의 페이징 옵션을 선택할 수 있습니다.";

    @ApiOperation(value = GET_COUPON_GROUP, notes = GET_COUPON_GROUP_FROM_ID_DESCRIPTION)
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
        AdminRoleValidator.checkAuthentication();
        Pageable pageable = paged ? PageRequest.of(page, size, parseSortString(sort)) : Pageable.unpaged();

        Page<Coupon> couponPage = couponGroupService.getCouponGroup(Long.parseLong(couponGroupId), pageable);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CouponGroupEntityToDtoMapper.toCouponGroupResponseDto(couponPage));
    }

    private Sort parseSortString(String sort) {
        String[] parts = sort.split(",");
        return Sort.by(Sort.Direction.fromString(parts[1]), parts[0]);
    }
}
