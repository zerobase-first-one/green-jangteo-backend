package com.firstone.greenjangteo.web;

public class ApiConstant {
    public static final String USER_ID_VALUE = "회원 ID";

    public static final String EMAIL_EXAMPLE = "abcd@abc.com";
    public static final String PASSWORD_VALUE = "비밀번호";
    public static final String PASSWORD_CONFIRM_VALUE = "비밀번호 확인";
    public static final String PASSWORD_EXAMPLE = "Abc1!2@34";
    public static final String ID_EXAMPLE = "1";

    public static final String PRINCIPAL_POINTCUT
            = "isAuthenticated() and (( #userId == principal.username ) or hasRole('ROLE_ADMIN'))";
}