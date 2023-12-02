package com.firstone.greenjangteo.user.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SignInForm {
    @ApiModelProperty(value = "email or username", example = "abcd@abc.com")
    private String emailOrUsername;

    @ApiModelProperty(value = "password", example = "Abc1!2@34")
    private String password;
}
