package com.firstone.greenjangteo.user.service;

import com.firstone.greenjangteo.user.dto.request.DeleteRequestDto;
import com.firstone.greenjangteo.user.dto.request.EmailRequestDto;
import com.firstone.greenjangteo.user.dto.request.PasswordUpdateRequestDto;
import com.firstone.greenjangteo.user.dto.request.PhoneRequestDto;
import com.firstone.greenjangteo.user.form.SignInForm;
import com.firstone.greenjangteo.user.form.SignUpForm;
import com.firstone.greenjangteo.user.model.entity.User;

/**
 * 인증이 필요한 서비스
 */
public interface AuthenticationService {
    User signUpUser(SignUpForm signUpForm);

    User signInUser(SignInForm signInForm);

    void updateEmail(Long id, EmailRequestDto emailRequestDto);

    void updatePhone(Long id, PhoneRequestDto phoneRequestDto);

    void updatePassword(Long id, PasswordUpdateRequestDto passwordUpdateRequestDto);

    void deleteUser(long id, DeleteRequestDto deleteRequestDto);
}
