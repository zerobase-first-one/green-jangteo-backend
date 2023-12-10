package com.firstone.greenjangteo.user.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class SignInForm {
    @ApiModelProperty(value = "이메일 주소 또는 사용자 이름(아이디)", example = "abcd@abc.com")
    private String emailOrUsername;

    @ApiModelProperty(value = "비밀번호", example = "Abc1!2@34")
    private String password;
}
