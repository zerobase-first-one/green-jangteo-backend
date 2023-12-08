package com.firstone.greenjangteo.user.domain.store.exception;

public class ExceptionMessage {
    public static final String STORE_NAME_NO_VALUE_EXCEPTION = "가게 이름은 필수값입니다.";
    public static final String INVALID_STORE_NAME_EXCEPTION =
            "가게 이름은 20자 이하의 한글 또는 영문 대소문자로 구성되어야 합니다. 예: 친환경 스토어";
    public static final String STORE_NOT_FOUND_EXCEPTION = "해당 가게가 존재하지 않습니다. 판매자 ID: ";
    public static final String DUPLICATE_STORE_NAME_EXCEPTION = "중복된 가게 이름입니다. 가게 이름: ";
}
