package com.firstone.greenjangteo.user.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class UserResponseDto {
    private Long userId;
    private String email;
    private String username;
    private String fullName;
    private String phone;
    private AddressDto addressDto;
    private List<String> roles;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createdAt;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime modifiedAt;

    public static UserResponseDto of(Long userId, LocalDateTime createdAt) {
        return UserResponseDto.builder()
                .userId(userId)
                .createdAt(createdAt)
                .build();
    }
}
