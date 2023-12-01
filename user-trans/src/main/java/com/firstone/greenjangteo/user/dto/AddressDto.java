package com.firstone.greenjangteo.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AddressDto {
    @ApiModelProperty(value = "지역 이름", example = "서울")
    private String city;

    @ApiModelProperty(value = "도로명 주소", example = "테헤란로 231")
    private String street;

    @ApiModelProperty(value = "우편번호", example = "06142")
    private String zipcode;

    @ApiModelProperty(value = "상세 주소", example = "길동아파트 101동 102호")
    private String detailedAddress;
}
