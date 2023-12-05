package com.firstone.greenjangteo.user.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PhoneRequestDto {
    @ApiModelProperty(value = "비밀번호", example = "Abc1!2@34")
    private String password;

    @ApiModelProperty(value = "변경할 전화번호", example = "01012345679")
    private String phone;
}
