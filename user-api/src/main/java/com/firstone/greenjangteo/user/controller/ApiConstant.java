package com.firstone.greenjangteo.user.controller;

public class ApiConstant {
    public static final String USER_ID_FORM = "회원 ID";
    static final String PRINCIPAL_POINTCUT
            = "isAuthenticated() and (( #userId == principal.username ) or hasRole('ROLE_ADMIN'))";
}
