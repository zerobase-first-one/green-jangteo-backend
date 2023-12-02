package com.firstone.greenjangteo.user.form;

import com.firstone.greenjangteo.user.dto.AddressDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SignUpForm {
    @ApiModelProperty(value = "이메일 주소", example = "abcd@abc.com")
    private String email;

    @ApiModelProperty(value = "사용자 이름(아이디)", example = "tester1")
    private String username;

    @ApiModelProperty(value = "비밀번호", example = "Abc1!2@34")
    private String password;

    @ApiModelProperty(value = "성명", example = "홍길동")
    private String fullName;

    @ApiModelProperty(value = "전화번호", example = "01012345678")
    private String phone;

    @ApiModelProperty("주소")
    private AddressDto addressDto;

    @ApiModelProperty(value = "분류", example = "[ROLE_BUYER, ROLE_SELLER]")
    private List<String> roles;
}
