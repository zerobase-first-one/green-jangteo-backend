package com.firstone.greenjangteo.user.service;

import com.firstone.greenjangteo.user.dto.UserResponseDto;
import com.firstone.greenjangteo.user.form.SignUpForm;

/**
 * 인증이 필요한 서비스
 */
public interface AuthenticationService {
    UserResponseDto signUpUser(SignUpForm signUpForm);
}
