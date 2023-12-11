package com.firstone.greenjangteo.user.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import static com.firstone.greenjangteo.web.ApiConstant.PASSWORD_EXAMPLE;
import static com.firstone.greenjangteo.web.ApiConstant.PASSWORD_VALUE;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PhoneRequestDto {
    private static final String PHONE_TO_CHANGE_VALUE = "변경할 전화번호";
    private static final String PHONE_TO_CHANGE_EXAMPLE = "01012345678";

    @ApiModelProperty(value = PASSWORD_VALUE, example = PASSWORD_EXAMPLE)
    private String password;

    @ApiModelProperty(value = PHONE_TO_CHANGE_VALUE, example = PHONE_TO_CHANGE_EXAMPLE)
    private String phone;
}
