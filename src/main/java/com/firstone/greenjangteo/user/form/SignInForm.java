package com.firstone.greenjangteo.user.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import static com.firstone.greenjangteo.web.ApiConstant.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class SignInForm {
    private static final String EMAIL_OR_USERNAME_VALUE = "이메일 주소 또는 사용자 이름(아이디)";

    @ApiModelProperty(value = EMAIL_OR_USERNAME_VALUE, example = EMAIL_EXAMPLE)
    private String emailOrUsername;

    @ApiModelProperty(value = PASSWORD_VALUE, example = PASSWORD_EXAMPLE)
    private String password;
}
