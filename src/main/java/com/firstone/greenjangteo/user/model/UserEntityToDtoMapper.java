package com.firstone.greenjangteo.user.model;

import com.firstone.greenjangteo.order.dto.response.BuyerResponseDto;
import com.firstone.greenjangteo.user.dto.response.UserResponseDto;
import com.firstone.greenjangteo.user.model.entity.User;

public class UserEntityToDtoMapper {
    public static UserResponseDto toPrincipal(User user) {
        return UserResponseDto.builder()
                .email(user.getEmail().getValue())
                .username(user.getUsername().getValue())
                .fullName(user.getFullName().getValue())
                .phone(user.getPhone().getValue())
                .addressDto(user.getAddress().toDto())
                .roles(user.getRoles().toStrings())
                .createdAt(user.getCreatedAt())
                .modifiedAt(user.getModifiedAt())
                .build();
    }

    public static UserResponseDto toOthers(User user) {
        return UserResponseDto.builder()
                .username(user.getUsername().getValue())
                .roles(user.getRoles().toStrings())
                .createdAt(user.getCreatedAt())
                .modifiedAt(user.getModifiedAt())
                .build();
    }

    public static BuyerResponseDto toOrder(User buyer) {
        return BuyerResponseDto.builder()
                .buyerName(buyer.getFullName().getValue())
                .buyerPhone(buyer.getPhone().getValue())
                .shippingAddressDto(buyer.getAddress().toDto())
                .build();
    }
}
