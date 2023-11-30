package com.firstone.greenjangteo.user.service;


import com.firstone.greenjangteo.user.dto.UserResponseDto;
import com.firstone.greenjangteo.user.excpeption.general.DuplicateUserException;
import com.firstone.greenjangteo.user.excpeption.general.DuplicateUsernameException;
import com.firstone.greenjangteo.user.form.SignUpForm;
import com.firstone.greenjangteo.user.model.Email;
import com.firstone.greenjangteo.user.model.Phone;
import com.firstone.greenjangteo.user.model.Username;
import com.firstone.greenjangteo.user.model.entity.User;
import com.firstone.greenjangteo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.firstone.greenjangteo.user.excpeption.message.DuplicateExceptionMessage.*;
import static org.springframework.transaction.annotation.Isolation.REPEATABLE_READ;

/**
 * 인증이 필요한 서비스
 */
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(isolation = REPEATABLE_READ, timeout = 20)
    public UserResponseDto signUpUser(SignUpForm signUpForm) {
        User user = User.from(signUpForm, passwordEncoder);

        validateNotDuplicateUser(signUpForm.getUsername(), signUpForm.getEmail(), signUpForm.getPhone());

        User savedUser = userRepository.save(user);

        return UserResponseDto.of(savedUser.getId(), savedUser.getCreatedAt());
    }

    private void validateNotDuplicateUser(String username, String email, String phone) {
        checkUsername(username);
        checkEmail(email);
        checkPhone(phone);
    }

    private void checkUsername(String username) {
        if (userRepository.existsByUsername(Username.of(username))) {
            throw new DuplicateUsernameException(DUPLICATE_USERNAME_EXCEPTION + username);
        }
    }

    private void checkEmail(String email) {
        if (userRepository.existsByEmail(Email.of(email))) {
            throw new DuplicateUserException(DUPLICATE_EMAIL_EXCEPTION + email);
        }
    }

    private void checkPhone(String phone) {
        if (userRepository.existsByPhone(Phone.of(phone))) {
            throw new DuplicateUserException(DUPLICATE_PHONE_EXCEPTION + phone);
        }
    }
}
