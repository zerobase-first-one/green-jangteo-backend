package com.firstone.greenjangteo.user.controller;

import com.firstone.greenjangteo.user.dto.UserResponseDto;
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

/**
 * 인증이 필요한 API
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    private static final String SIGN_UP = "회원 가입";
    private static final String SIGN_UP_DESCRIPTION = "회원 가입 양식을 입력해 회원 가입을 할 수 있습니다.";
    private static final String SIGN_UP_FORM = "회원 가입 양식";

    @ApiOperation(value = SIGN_UP, notes = SIGN_UP_DESCRIPTION)
    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signUpUser
            (@RequestBody @ApiParam(value = SIGN_UP_FORM) SignUpForm signUpForm) {
        User user = authenticationService.signUpUser(signUpForm);

        return buildResponse(UserResponseDto.of(user.getId(), user.getCreatedAt()));
    }

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
