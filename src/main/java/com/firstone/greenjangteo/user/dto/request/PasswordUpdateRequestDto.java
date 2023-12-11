package com.firstone.greenjangteo.user.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import static com.firstone.greenjangteo.web.ApiConstant.PASSWORD_EXAMPLE;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PasswordUpdateRequestDto {
    private static final String CURRENT_PASSWORD_VALUE = "현재 비밀번호";
    private static final String PASSWORD_TO_CHANGE_VALUE = "변경할 비밀번호";
    private static final String PASSWORD_TO_CHANGE_CONFIRM_VALUE = "변경할 비밀번호 확인";
    private static final String PASSWORD_TO_CHANGE_EXAMPLE = "Abc1!2@34";

    @ApiModelProperty(value = CURRENT_PASSWORD_VALUE, example = PASSWORD_EXAMPLE)
    private String currentPassword;

    @ApiModelProperty(value = PASSWORD_TO_CHANGE_VALUE, example = PASSWORD_TO_CHANGE_EXAMPLE)
    private String passwordToChange;

    @ApiModelProperty(value = PASSWORD_TO_CHANGE_CONFIRM_VALUE, example = PASSWORD_TO_CHANGE_EXAMPLE)
    private String passwordToChangeConfirm;
}
