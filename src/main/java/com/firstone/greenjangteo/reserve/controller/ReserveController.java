package com.firstone.greenjangteo.reserve.controller;

import com.firstone.greenjangteo.post.dto.PostResponseDto;
import com.firstone.greenjangteo.reserve.dto.request.AddReserveRequestDto;
import com.firstone.greenjangteo.reserve.dto.request.UseReserveRequestDto;
import com.firstone.greenjangteo.reserve.dto.response.ReserveResponseDto;
import com.firstone.greenjangteo.reserve.model.entity.ReserveHistory;
import com.firstone.greenjangteo.reserve.service.ReserveService;
import com.firstone.greenjangteo.utility.InputFormatValidator;
import com.firstone.greenjangteo.utility.RoleValidator;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.firstone.greenjangteo.web.ApiConstant.*;

@RestController
@RequestMapping("/reserves")
@RequiredArgsConstructor
public class ReserveController {
    private final ReserveService reserveService;

    private static final String ADD_RESERVE = "적립금 추가";
    private static final String ADD_RESERVE_DESCRIPTION = "회원 ID와 적립 예정 금액을 입력해 적립금을 추가할 수 있습니다.";

    private static final String REDUCE_RESERVE = "적립금 차감";
    private static final String REDUCE_RESERVE_DESCRIPTION = "회원 ID와 차감 예정 금액을 입력해 적립금을 차감할 수 있습니다.";

    private static final String GET_RESERVE_HISTORIES = "적립금 히스토리 조회";
    private static final String GET_RESERVE_HISTORIES_DESCRIPTION
            = "회원 ID를 입력해 적립금의 적립, 사용 내역을 조회할 수 있습니다.";

    private static final String GET_CURRENT_RESERVE = "현재 적립금 조회";
    private static final String GET_CURRENT_RESERVE_DESCRIPTION = "회원 ID를 입력해 현재 적립금을 조회할 수 있습니다.";

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

    @ApiOperation(value = GET_RESERVE_HISTORIES, notes = GET_RESERVE_HISTORIES_DESCRIPTION)
    @PreAuthorize(PRINCIPAL_POINTCUT)
    @GetMapping()
    public ResponseEntity<List<ReserveResponseDto>> getReserveHistories
            (@RequestParam(name = "userId") @ApiParam(value = USER_ID_VALUE, example = ID_EXAMPLE) String userId) {
        InputFormatValidator.validateId(userId);

        List<ReserveHistory> reserveHistories = reserveService.getReserveHistories(Long.parseLong(userId));
        List<ReserveResponseDto> reserveResponseDtos
                = reserveHistories.stream().map(ReserveResponseDto::from).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(reserveResponseDtos);
    }

    @ApiOperation(value = GET_CURRENT_RESERVE, notes = GET_CURRENT_RESERVE_DESCRIPTION)
    @PreAuthorize(PRINCIPAL_POINTCUT)
    @GetMapping("/current")
    public ResponseEntity<ReserveResponseDto> getCurrentResolve
            (@RequestParam(name = "userId") @ApiParam(value = USER_ID_VALUE, example = ID_EXAMPLE) String userId) {
        InputFormatValidator.validateId(userId);

        ReserveHistory reserveHistory = reserveService.getCurrentReserve(Long.parseLong(userId));
        return ResponseEntity.status(HttpStatus.OK).body(ReserveResponseDto.from(reserveHistory));
    }
}
