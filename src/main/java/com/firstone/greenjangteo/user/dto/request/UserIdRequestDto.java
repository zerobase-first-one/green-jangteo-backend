package com.firstone.greenjangteo.user.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.firstone.greenjangteo.web.ApiConstant.ID_EXAMPLE;
import static com.firstone.greenjangteo.web.ApiConstant.USER_ID_VALUE;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class UserIdRequestDto {
    @ApiModelProperty(value = USER_ID_VALUE, example = ID_EXAMPLE)
    private String userId;
}
