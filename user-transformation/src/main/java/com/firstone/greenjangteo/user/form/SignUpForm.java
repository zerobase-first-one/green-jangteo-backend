package com.firstone.greenjangteo.user.form;

import com.firstone.greenjangteo.user.model.embedment.Address;
import com.firstone.greenjangteo.user.model.entity.Role;
import com.firstone.greenjangteo.user.validation.group.OnSignUp;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SignUpForm {
    @ApiModelProperty(value = "이메일 주소", example = "abcd@abc.com")
    @NotBlank(groups = OnSignUp.class, message = "이메일 주소는 필수값입니다.")
    @Email(groups = OnSignUp.class, message = "이메일 주소 형식이 잘못되었습니다. 예: abcd@abc.com")
    private String email;

    @ApiModelProperty(value = "사용자 이름(아이디)", example = "tester1")
    @NotBlank(groups = OnSignUp.class, message = "사용자 이름은 필수값입니다.")
    @Pattern(groups = OnSignUp.class, regexp = "^[a-z0-9]{4,16}$",
            message = "아이디는 4~16자 이하의 영소문자와 숫자만으로 구성되어야 합니다. 예: tester1")
    private String username;

    @ApiModelProperty(value = "비밀번호", example = "1!2@34")
    @NotBlank(groups = OnSignUp.class, message = "비밀번호는 필수값입니다.")
    @Pattern(groups = OnSignUp.class,
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "사용자 이름은 하나 이상의 대문자와 소문자, 숫자, 특수문자를 포함해야 합니다. 예: Test1!")
    private String password;

    @ApiModelProperty(value = "성명", example = "홍길동")
    @NotBlank(groups = OnSignUp.class, message = "성명은 필수값입니다.")
    private String fullName;

    @ApiModelProperty(value = "전화번호", example = "01012345678")
    @Pattern(groups = OnSignUp.class, regexp = "^010\\d{8}$", message = "전화번호 형식이 잘못되었습니다. 예: 01012345678")
    private String phone;

    @Valid
    private Address address;

    @ApiModelProperty(value = "분류", example = "[ROLE_GENERAL_USER, ROLE_BUSINESS_USER]")
    @NotNull(groups = OnSignUp.class, message = "회원 분류를 선택해 주세요.")
    private List<Role> roles;
}
