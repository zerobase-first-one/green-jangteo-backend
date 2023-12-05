package com.firstone.greenjangteo.user.controller;

import com.firstone.greenjangteo.common.security.JwtTokenProvider;
import com.firstone.greenjangteo.user.dto.SignInResponseDto;
import com.firstone.greenjangteo.user.dto.UserResponseDto;
import com.firstone.greenjangteo.user.form.SignInForm;
import com.firstone.greenjangteo.user.form.SignUpForm;
import com.firstone.greenjangteo.user.model.entity.User;
import com.firstone.greenjangteo.user.service.AuthenticationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;

/**
 * 인증이 필요한 API
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final JwtTokenProvider jwtTokenProvider;

    private static final String SIGN_UP = "회원 가입";
    private static final String SIGN_UP_DESCRIPTION = "회원 가입 양식을 입력해 회원 가입을 할 수 있습니다.";
    private static final String SIGN_UP_FORM = "회원 가입 양식";
    private static final String SIGN_IN = "로그인";
    private static final String SIGN_IN_DESCRIPTION = "email 또는 username과 비밀번호를 입력해 로그인을 할 수 있습니다.";
    private static final String SIGN_IN_FORM = "로그인 양식";


    @ApiOperation(value = SIGN_UP, notes = SIGN_UP_DESCRIPTION)
    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signUpUser
            (@RequestBody @ApiParam(value = SIGN_UP_FORM) SignUpForm signUpForm) {
        User user = authenticationService.signUpUser(signUpForm);

        return buildResponse(UserResponseDto.of(user.getId(), user.getCreatedAt()));
    }

    @ApiOperation(value = SIGN_IN, notes = SIGN_IN_DESCRIPTION)
    @PostMapping("/login")
    public ResponseEntity<SignInResponseDto> signInUser(@RequestBody @ApiParam(value = SIGN_IN_FORM) SignInForm signInForm) {
        User user = authenticationService.signInUser(signInForm);

        Long userId = user.getId();
        LocalDateTime loggedInTime = user.getLastLoggedInAt();
        String token = jwtTokenProvider.generateToken(String.valueOf(user.getId()), user.getRoles().toStrings());
        ;

        return ResponseEntity.status(HttpStatus.OK).body(new SignInResponseDto(userId, loggedInTime, token));
    }

    private ResponseEntity<UserResponseDto> buildResponse(UserResponseDto userResponseDto) {
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{userId}")
                .buildAndExpand(userResponseDto.getUserId())
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);

        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(userResponseDto);
    }
}
