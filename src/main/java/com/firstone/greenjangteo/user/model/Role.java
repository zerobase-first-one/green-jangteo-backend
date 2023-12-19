package com.firstone.greenjangteo.user.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Role {
    ROLE_ADMIN("관리자"),
    ROLE_SELLER("판매자"),
    ROLE_BUYER("구매자");

    private final String description;
}
