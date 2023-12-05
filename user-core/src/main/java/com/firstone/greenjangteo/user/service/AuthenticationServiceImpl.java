package com.firstone.greenjangteo.user.service;


import com.firstone.greenjangteo.user.excpeption.general.DuplicateUserException;
import com.firstone.greenjangteo.user.excpeption.general.DuplicateUsernameException;
import com.firstone.greenjangteo.user.excpeption.significant.IncorrectPasswordException;
import com.firstone.greenjangteo.user.form.SignInForm;
import com.firstone.greenjangteo.user.form.SignUpForm;
import com.firstone.greenjangteo.user.model.Email;
import com.firstone.greenjangteo.user.model.Phone;
import com.firstone.greenjangteo.user.model.Username;
import com.firstone.greenjangteo.user.model.entity.User;
import com.firstone.greenjangteo.user.model.security.CustomUserDetails;
import com.firstone.greenjangteo.user.model.security.Password;
import com.firstone.greenjangteo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

import static com.firstone.greenjangteo.user.excpeption.message.DuplicateExceptionMessage.*;
import static com.firstone.greenjangteo.user.excpeption.message.NotFoundExceptionMessage.EMAIL_NOT_FOUND_EXCEPTION;
import static com.firstone.greenjangteo.user.excpeption.message.NotFoundExceptionMessage.USERNAME_NOT_FOUND_EXCEPTION;
import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Isolation.REPEATABLE_READ;

/**
 * 인증이 필요한 서비스
 */
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService, UserDetailsService {
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        return new CustomUserDetails(userService.getUser(Long.parseLong(userId)));
    }

    @Override
    @Transactional(isolation = REPEATABLE_READ, timeout = 20)
    public User signUpUser(SignUpForm signUpForm) {
        User user = User.from(signUpForm, passwordEncoder);

        validateNotDuplicateUser(signUpForm.getUsername(), signUpForm.getEmail(), signUpForm.getPhone());

        return userRepository.save(user);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED, readOnly = true, timeout = 10)
    public User signInUser(SignInForm signInForm) {
        User signedUpUser = getUserFromEmailOrUsername(signInForm.getEmailOrUsername());

        validatePassword(signedUpUser.getPassword(), signInForm.getPassword());
        signedUpUser.updateLoginTime();

        return signedUpUser;
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

    private User getUserFromEmailOrUsername(String emailOrUsername) {
        return emailOrUsername.contains("@")
                ? userRepository.findByEmail(Email.of(emailOrUsername))
                .orElseThrow(() -> new EntityNotFoundException
                        (EMAIL_NOT_FOUND_EXCEPTION + emailOrUsername))
                : userRepository.findByUsername(Username.of(emailOrUsername))
                .orElseThrow(() -> new EntityNotFoundException
                        (USERNAME_NOT_FOUND_EXCEPTION + emailOrUsername));
    }

    private void validatePassword(Password certifiedPassword, String enteredPassword) {
        if (!certifiedPassword.matchOriginalPassword(passwordEncoder, enteredPassword)) {
            throw new IncorrectPasswordException();
        }
    }
}
