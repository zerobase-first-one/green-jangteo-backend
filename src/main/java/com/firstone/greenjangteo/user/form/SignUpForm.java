package com.firstone.greenjangteo.user.form;

import com.firstone.greenjangteo.user.dto.AddressDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

import static com.firstone.greenjangteo.web.ApiConstant.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class SignUpForm {
    private static final String EMAIL_VALUE = "이메일 주소";

    public static final String USERNAME_VALUE = "사용자 이름(아이디)";
    private static final String USERNAME_EXAMPLE = "tester1";

    private static final String FULL_NAME_VALUE = "성명";
    private static final String FULL_NAME_EXAMPLE = "홍길동";

    private static final String PHONE_VALUE = "전화번호";
    private static final String PHONE_EXAMPLE = "01012345678";

    private static final String ADDRESS_VALUE = "주소";

    private static final String ROLES_VALUE = "분류";
    private static final String ROLES_EXAMPLE = "[ROLE_ADMIN, ROLE_BUYER, ROLE_SELLER]";

    private static final String STORE_NAME_VALUE = "가게 이름";
    private static final String STORE_NAME_EXAMPLE = "친환경 스토어";


    @ApiModelProperty(value = EMAIL_VALUE, example = EMAIL_EXAMPLE)
    private String email;

    @ApiModelProperty(value = USERNAME_VALUE, example = USERNAME_EXAMPLE)
    private String username;

    @ApiModelProperty(value = PASSWORD_VALUE, example = PASSWORD_EXAMPLE)
    private String password;

    @ApiModelProperty(value = PASSWORD_CONFIRM_VALUE, example = PASSWORD_EXAMPLE)
    private String passwordConfirm;

    @ApiModelProperty(value = FULL_NAME_VALUE, example = FULL_NAME_EXAMPLE)
    private String fullName;

    @ApiModelProperty(value = PHONE_VALUE, example = PHONE_EXAMPLE)
    private String phone;

    @ApiModelProperty(ADDRESS_VALUE)
    private AddressDto addressDto;

    @ApiModelProperty(value = ROLES_VALUE, example = ROLES_EXAMPLE)
    private List<String> roles;

    @ApiModelProperty(value = STORE_NAME_VALUE, example = STORE_NAME_EXAMPLE)
    private String storeName;
}
