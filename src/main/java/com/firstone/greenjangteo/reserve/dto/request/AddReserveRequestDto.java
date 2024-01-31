package com.firstone.greenjangteo.reserve.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.firstone.greenjangteo.web.ApiConstant.ID_EXAMPLE;
import static com.firstone.greenjangteo.web.ApiConstant.USER_ID_VALUE;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddReserveRequestDto {
    private static final String USED_RESERVE_VALUE = "추가할 적립금";
    static final String USED_RESERVE_EXAMPLE = "2000";

    @ApiModelProperty(value = USER_ID_VALUE, example = ID_EXAMPLE)
    private String userId;

    @ApiModelProperty(value = USED_RESERVE_VALUE, example = USED_RESERVE_EXAMPLE)
    private int addedReserve;
}
