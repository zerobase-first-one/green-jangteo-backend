package com.firstone.greenjangteo.user.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class EmailRequestDto {
    @ApiModelProperty(value = "비밀번호", example = "Abc1!2@34")
    private String password;

    @ApiModelProperty(value = "변경할 이메일 주소", example = "abcde@abc.com")
    private String email;
}
