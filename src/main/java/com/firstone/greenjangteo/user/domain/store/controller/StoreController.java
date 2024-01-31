package com.firstone.greenjangteo.user.domain.store.controller;

import com.firstone.greenjangteo.user.domain.store.dto.StoreRequestDto;
import com.firstone.greenjangteo.user.domain.store.dto.StoreResponseDto;
import com.firstone.greenjangteo.user.domain.store.model.entity.Store;
import com.firstone.greenjangteo.user.domain.store.service.StoreService;
import com.firstone.greenjangteo.utility.InputFormatValidator;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.firstone.greenjangteo.web.ApiConstant.*;

@RestController
@RequestMapping("/stores")
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;

    private static final String GET_STORE = "자신의 가게 조회";
    private static final String GET_STORE_DESCRIPTION = "자신의 가게를 조회할 수 있습니다.";

    private static final String UPDATE_STORE = "자신의 가게 수정";
    private static final String UPDATE_STORE_DESCRIPTION = "자신의 가게를 수정할 수 있습니다.";
    private static final String UPDATE_STORE_FORM = "가게 수정 양식";

    @ApiOperation(value = GET_STORE, notes = GET_STORE_DESCRIPTION)
    @PreAuthorize(PRINCIPAL_POINTCUT)
    @GetMapping("/{userId}")
    public ResponseEntity<StoreResponseDto> getStore
            (@PathVariable("userId") @ApiParam(value = USER_ID_VALUE, example = ID_EXAMPLE) String userId) {
        InputFormatValidator.validateId(userId);
        Store store = storeService.getStore(Long.parseLong(userId));

        return ResponseEntity.status(HttpStatus.OK).body(StoreResponseDto.from(store));
    }

    @ApiOperation(value = UPDATE_STORE, notes = UPDATE_STORE_DESCRIPTION)
    @PreAuthorize(PRINCIPAL_POINTCUT)
    @PutMapping("/{userId}")
    public ResponseEntity<Void> updateStore
            (@PathVariable("userId") @ApiParam(value = USER_ID_VALUE, example = ID_EXAMPLE) String userId,
             @RequestBody @ApiParam(value = UPDATE_STORE_FORM) StoreRequestDto storeRequestDto) {
        InputFormatValidator.validateId(userId);
        storeService.updateStore(Long.parseLong(userId), storeRequestDto);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
