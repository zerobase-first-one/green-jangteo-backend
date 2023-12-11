package com.firstone.greenjangteo.user.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PasswordUpdateRequestDto {
    @ApiModelProperty(value = "현재 비밀번호", example = "Abc1!2@34")
    private String currentPassword;

    @ApiModelProperty(value = "변경할 비밀번호", example = "Abcd1!2@34")
    private String passwordToChange;

    @ApiModelProperty(value = "변경할 비밀번호 확인", example = "Abcd1!2@34")
    private String passwordToChangeConfirm;
}
