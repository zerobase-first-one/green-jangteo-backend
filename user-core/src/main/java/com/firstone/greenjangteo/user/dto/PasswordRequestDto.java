package com.firstone.greenjangteo.user.dto;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PasswordRequestDto {
    private String currentPassword;
    private String passwordToChange;
}
