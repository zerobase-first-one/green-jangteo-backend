package com.firstone.greenjangteo.user.service;

import com.firstone.greenjangteo.user.form.SignInForm;
import com.firstone.greenjangteo.user.form.SignUpForm;
import com.firstone.greenjangteo.user.model.entity.User;

/**
 * 인증이 필요한 서비스
 */
public interface AuthenticationService {
    User signUpUser(SignUpForm signUpForm);

    User signInUser(SignInForm signInForm);
}
