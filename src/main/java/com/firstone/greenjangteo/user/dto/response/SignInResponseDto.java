package com.firstone.greenjangteo.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class SignInResponseDto {
    private final Long id;
    private final LocalDateTime loggedInAt;
    private final String token;
}
