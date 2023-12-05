package com.firstone.greenjangteo.user.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class DeleteRequestDto {
    @ApiModelProperty(value = "비밀번호", example = "Abc1!2@34")
    private String password;
}
