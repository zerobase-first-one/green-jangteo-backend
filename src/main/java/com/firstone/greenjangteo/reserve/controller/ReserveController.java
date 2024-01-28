package com.firstone.greenjangteo.reserve.controller;

import com.firstone.greenjangteo.post.dto.PostResponseDto;
import com.firstone.greenjangteo.reserve.dto.request.AddReserveRequestDto;
import com.firstone.greenjangteo.reserve.dto.request.UseReserveRequestDto;
import com.firstone.greenjangteo.reserve.service.ReserveService;
import com.firstone.greenjangteo.utility.InputFormatValidator;
import com.firstone.greenjangteo.utility.RoleValidator;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reserves")
@RequiredArgsConstructor
public class ReserveController {
    private final ReserveService reserveService;

    private static final String ADD_RESERVE = "적립금 추가";
    private static final String ADD_RESERVE_DESCRIPTION = "회원 ID와 적립 예정 금액을 입력해 적립금을 추가할 수 있습니다.";

    private static final String REDUCE_RESERVE = "적립금 차감";
    private static final String REDUCE_RESERVE_DESCRIPTION = "회원 ID와 차감 예정 금액을 입력해 적립금을 차감할 수 있습니다.";

    @ApiOperation(value = ADD_RESERVE, notes = ADD_RESERVE_DESCRIPTION)
    @PostMapping("/add")
    public ResponseEntity<PostResponseDto> addReserve(@RequestBody AddReserveRequestDto addReserveRequestDto) {
        InputFormatValidator.validateId(addReserveRequestDto.getUserId());
        RoleValidator.checkAdminAuthentication();

        reserveService.addReserve(addReserveRequestDto);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @ApiOperation(value = REDUCE_RESERVE, notes = REDUCE_RESERVE_DESCRIPTION)
    @PostMapping("/reduce")
    public ResponseEntity<PostResponseDto> reduceReserve(@RequestBody UseReserveRequestDto useReserveRequestDto) {
        InputFormatValidator.validateId(useReserveRequestDto.getUserId());
        RoleValidator.checkAdminAuthentication();

        reserveService.reduceReserve(useReserveRequestDto);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
