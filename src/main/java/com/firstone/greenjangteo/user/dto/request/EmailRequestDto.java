package com.firstone.greenjangteo.user.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import static com.firstone.greenjangteo.web.ApiConstant.PASSWORD_EXAMPLE;
import static com.firstone.greenjangteo.web.ApiConstant.PASSWORD_VALUE;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class EmailRequestDto {
    private static final String EMAIL_TO_CHANGE_VALUE = "변경할 이메일 주소";
    private static final String EMAIL_TO_CHANGE_EXAMPLE = "abcde@abc.com";

    @ApiModelProperty(value = PASSWORD_VALUE, example = PASSWORD_EXAMPLE)
    private String password;

    @ApiModelProperty(value = EMAIL_TO_CHANGE_VALUE, example = EMAIL_TO_CHANGE_EXAMPLE)
    private String email;
}
