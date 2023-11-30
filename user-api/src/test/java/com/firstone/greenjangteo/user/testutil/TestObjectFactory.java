package com.firstone.greenjangteo.user.testutil;

import com.firstone.greenjangteo.user.dto.AddressDto;
import com.firstone.greenjangteo.user.form.SignUpForm;

import java.util.List;

public class TestObjectFactory {
    public static SignUpForm enterUserForm(String email, String username, String password, String fullName,
                                           String phone, List<String> roles) {
        return SignUpForm.builder()
                .email(email)
                .username(username)
                .password(password)
                .fullName(fullName)
                .phone(phone)
                .addressDto(AddressDto.builder()
                        .city("서울")
                        .street("테헤란로 2길 5")
                        .zipcode("12345")
                        .detailedAddress("101동 102호")
                        .build())
                .roles(roles)
                .build();
    }
}
