package com.firstone.greenjangteo.user.dto;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PhoneRequestDto {
    private String password;
    private String phone;
}
