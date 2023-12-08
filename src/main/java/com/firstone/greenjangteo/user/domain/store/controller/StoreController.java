package com.firstone.greenjangteo.user.domain.store.controller;

import com.firstone.greenjangteo.user.domain.store.dto.StoreDto;
import com.firstone.greenjangteo.user.domain.store.model.entity.Store;
import com.firstone.greenjangteo.user.domain.store.service.StoreService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.firstone.greenjangteo.user.controller.ApiConstant.PRINCIPAL_POINTCUT;
import static com.firstone.greenjangteo.user.controller.ApiConstant.USER_ID_FORM;

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
    public ResponseEntity<StoreDto> getStore
            (@PathVariable("userId") @ApiParam(value = USER_ID_FORM, example = "1") String userId) {
        Store store = storeService.getStore(Long.parseLong(userId));

        return ResponseEntity.status(HttpStatus.OK).body(StoreDto.from(store));
    }

    @ApiOperation(value = UPDATE_STORE, notes = UPDATE_STORE_DESCRIPTION)
    @PreAuthorize(PRINCIPAL_POINTCUT)
    @PutMapping("/{userId}")
    public ResponseEntity<Void> updateStore
            (@PathVariable("userId") @ApiParam(value = USER_ID_FORM, example = "1") String userId,
             @RequestBody @ApiParam(value = UPDATE_STORE_FORM) StoreDto storeDto) {
        storeService.updateStore(Long.parseLong(userId), storeDto);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
