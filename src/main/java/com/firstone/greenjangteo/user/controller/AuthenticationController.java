package com.firstone.greenjangteo.user.controller;

import com.firstone.greenjangteo.user.domain.token.service.TokenService;
import com.firstone.greenjangteo.user.dto.request.DeleteRequestDto;
import com.firstone.greenjangteo.user.dto.request.EmailRequestDto;
import com.firstone.greenjangteo.user.dto.request.PasswordUpdateRequestDto;
import com.firstone.greenjangteo.user.dto.request.PhoneRequestDto;
import com.firstone.greenjangteo.user.dto.response.SignInResponseDto;
import com.firstone.greenjangteo.user.dto.response.SignUpResponseDto;
import com.firstone.greenjangteo.user.form.SignInForm;
import com.firstone.greenjangteo.user.form.SignUpForm;
import com.firstone.greenjangteo.user.model.entity.User;
import com.firstone.greenjangteo.user.service.AuthenticationService;
import com.firstone.greenjangteo.utility.InputFormatValidator;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import static com.firstone.greenjangteo.web.ApiConstant.*;

/**
 * 인증이 필요한 API
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final TokenService tokenService;

    private static final String SIGN_UP = "회원 가입";
    private static final String SIGN_UP_DESCRIPTION = "회원 가입 양식을 입력해 회원 가입을 할 수 있습니다.";
    private static final String SIGN_UP_FORM = "회원 가입 양식";

    private static final String SIGN_IN = "로그인";
    private static final String SIGN_IN_DESCRIPTION = "email 또는 username과 비밀번호를 입력해 로그인을 할 수 있습니다.";
    private static final String SIGN_IN_FORM = "로그인 양식";

    private static final String UPDATE_EMAIL = "이메일 주소 변경";
    private static final String UPDATE_UPDATE_EMAIL_DESCRIPTION
            = "비밀번호와 변경할 이메일 주소를 입력해 이메일 주소를 수정할 수 있습니다.";
    private static final String UPDATE_UPDATE_EMAIL_FORM = "이메일 주소 변경 양식";

    private static final String UPDATE_PHONE = "전화번호 변경";
    private static final String UPDATE_PHONE_DESCRIPTION = "비밀번호와 변경할 전화번호를 입력해 전화번호를 수정할 수 있습니다.";
    private static final String UPDATE_PHONE_FORM = "전화번호 변경 양식";

    private static final String UPDATE_PASSWORD = "비밀번호 변경";
    private static final String UPDATE_PASSWORD_DESCRIPTION
            = "현재 비밀번호와 변경할 비밀번호를 입력해 비밀번호를 수정할 수 있습니다.";
    private static final String UPDATE_PASSWORD_FORM = "비밀번호 변경 양식";

    private static final String DELETE_USER = "회원 탈퇴";
    private static final String DELETE_USER_DESCRIPTION
            = "비밀번호를 입력해 회원을 탈퇴할 수 있습니다.";
    private static final String DELETE_USER_FORM = "회원 탈퇴 양식";

    @ApiOperation(value = SIGN_UP, notes = SIGN_UP_DESCRIPTION)
    @PostMapping("/signup")
    public ResponseEntity<SignUpResponseDto> signUpUser
            (@RequestBody @ApiParam(value = SIGN_UP_FORM) SignUpForm signUpForm) {
        User user = authenticationService.signUpUser(signUpForm);

        return buildResponse(new SignUpResponseDto(user.getId(), user.getCreatedAt()));
    }

    @ApiOperation(value = SIGN_IN, notes = SIGN_IN_DESCRIPTION)
    @PostMapping("/login")
    public ResponseEntity<SignInResponseDto> signInUser
            (@RequestBody @ApiParam(value = SIGN_IN_FORM) SignInForm signInForm) {
        User user = authenticationService.signInUser(signInForm);
        String accessToken = tokenService.issueAccessToken(user);
        String refreshToken = tokenService.issueRefreshToken(user);

        return ResponseEntity.status(HttpStatus.OK).body(SignInResponseDto.from(user, accessToken, refreshToken));
    }

    @ApiOperation(value = UPDATE_EMAIL, notes = UPDATE_UPDATE_EMAIL_DESCRIPTION)
    @PreAuthorize(PRINCIPAL_POINTCUT)
    @PatchMapping("/{userId}/email")
    public ResponseEntity<Void> updateEmail
            (@PathVariable("userId") @ApiParam(value = USER_ID_VALUE, example = ID_EXAMPLE) String userId,
             @RequestBody @ApiParam(value = UPDATE_UPDATE_EMAIL_FORM) EmailRequestDto emailRequestDto) {
        InputFormatValidator.validateId(userId);
        authenticationService.updateEmail(Long.parseLong(userId), emailRequestDto);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @ApiOperation(value = UPDATE_PHONE, notes = UPDATE_PHONE_DESCRIPTION)
    @PreAuthorize(PRINCIPAL_POINTCUT)
    @PatchMapping("/{userId}/phone")
    public ResponseEntity<Void> updatePhone
            (@PathVariable("userId") @ApiParam(value = USER_ID_VALUE, example = ID_EXAMPLE) String userId,
             @RequestBody @ApiParam(value = UPDATE_PHONE_FORM) PhoneRequestDto phoneRequestDto) {
        InputFormatValidator.validateId(userId);
        authenticationService.updatePhone(Long.parseLong(userId), phoneRequestDto);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @ApiOperation(value = UPDATE_PASSWORD, notes = UPDATE_PASSWORD_DESCRIPTION)
    @PreAuthorize(PRINCIPAL_POINTCUT)
    @PatchMapping("/{userId}/password")
    public ResponseEntity<Void> updatePassword
            (@PathVariable("userId") @ApiParam(value = USER_ID_VALUE, example = ID_EXAMPLE) String userId,
             @RequestBody @ApiParam(value = UPDATE_PASSWORD_FORM)
             PasswordUpdateRequestDto passwordUpdateRequestDto) {
        InputFormatValidator.validateId(userId);
        authenticationService.updatePassword(Long.parseLong(userId), passwordUpdateRequestDto);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @ApiOperation(value = DELETE_USER, notes = DELETE_USER_DESCRIPTION)
    @PreAuthorize(PRINCIPAL_POINTCUT)
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable("userId") @ApiParam(value = USER_ID_VALUE, example = ID_EXAMPLE) String userId,
            @RequestBody @ApiParam(value = DELETE_USER_FORM) DeleteRequestDto deleteRequestDto) {
        InputFormatValidator.validateId(userId);
        authenticationService.deleteUser(Long.parseLong(userId), deleteRequestDto);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private ResponseEntity<SignUpResponseDto> buildResponse(SignUpResponseDto signUpResponseDto) {
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{userId}")
                .buildAndExpand(signUpResponseDto.getUserId())
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);

        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(signUpResponseDto);
    }
}
