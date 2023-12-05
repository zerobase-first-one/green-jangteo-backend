package com.firstone.greenjangteo.user.controller;

import com.firstone.greenjangteo.user.dto.AddressDto;
import com.firstone.greenjangteo.user.dto.UserResponseDto;
import com.firstone.greenjangteo.user.model.EntityToDtoMapper;
import com.firstone.greenjangteo.user.model.entity.User;
import com.firstone.greenjangteo.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.firstone.greenjangteo.user.controller.ApiConstant.PRINCIPAL_POINTCUT;

/**
 * 회원 API
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    private static final String USER_ID_FORM = "회원 ID";

    private static final String GET_USER_DETAILS = "회원 개인정보 조회";
    private static final String GET_USER_DETAILS_DESCRIPTION = "회원 개인정보를 조회할 수 있습니다.";

    private static final String GET_USER = "회원 프로필 조회";
    private static final String GET_USER_DESCRIPTION = "다른 회원의 정보를 조회할 수 있습니다.";

    private static final String UPDATE_ADDRESS = "주소 변경";
    private static final String UPDATE_ADDRESS_DESCRIPTION = "변경할 주소를 입력해 주소를 수정할 수 있습니다.";
    private static final String UPDATE_ADDRESS_FORM = "주소 변경 양식";

    @ApiOperation(value = GET_USER_DETAILS, notes = GET_USER_DETAILS_DESCRIPTION)
    @PreAuthorize(PRINCIPAL_POINTCUT)
    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserResponseDto> getUserDetails
            (@PathVariable("userId") @ApiParam(value = USER_ID_FORM, example = "1") String userId) {
        User user = userService.getUser(Long.parseLong(userId));

        return ResponseEntity.status(HttpStatus.OK).body(EntityToDtoMapper.toPrincipal(user));
    }

    @ApiOperation(value = GET_USER, notes = GET_USER_DESCRIPTION)
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUser
            (@PathVariable("userId") @ApiParam(value = USER_ID_FORM, example = "1") String userId) {
        User user = userService.getUser(Long.parseLong(userId));

        return ResponseEntity.status(HttpStatus.OK).body(EntityToDtoMapper.toOthers(user));
    }

    @ApiOperation(value = UPDATE_ADDRESS, notes = UPDATE_ADDRESS_DESCRIPTION)
    @PreAuthorize(PRINCIPAL_POINTCUT)
    @PatchMapping("/{userId}/address")
    public ResponseEntity<UserResponseDto> updateAddress
            (@PathVariable("userId") @ApiParam(value = USER_ID_FORM, example = "1") String userId,
             @RequestBody @ApiParam(value = UPDATE_ADDRESS_FORM) AddressDto addressDto) {
        userService.updateAddress(Long.parseLong(userId), addressDto);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
