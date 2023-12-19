package com.firstone.greenjangteo.user.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.firstone.greenjangteo.user.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class SignInResponseDto {
    private final Long userId;

    private final List<String> roleDescriptions;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private final LocalDateTime loggedInAt;

    private final String token;

    public static SignInResponseDto from(User user, String token) {
        return SignInResponseDto.builder()
                .userId(user.getId())
                .roleDescriptions(user.getRoles().toDescriptions())
                .loggedInAt(user.getLastLoggedInAt())
                .token(token)
                .build();
    }
}
