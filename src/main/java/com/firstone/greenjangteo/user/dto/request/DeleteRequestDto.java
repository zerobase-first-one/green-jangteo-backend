package com.firstone.greenjangteo.user.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.firstone.greenjangteo.web.ApiConstant.PASSWORD_EXAMPLE;
import static com.firstone.greenjangteo.web.ApiConstant.PASSWORD_VALUE;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class DeleteRequestDto {
    @ApiModelProperty(value = PASSWORD_VALUE, example = PASSWORD_EXAMPLE)
    private String password;
}
