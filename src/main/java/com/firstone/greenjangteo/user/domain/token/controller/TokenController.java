package com.firstone.greenjangteo.user.domain.token.controller;

import com.firstone.greenjangteo.user.domain.token.service.TokenService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/token")
@RequiredArgsConstructor
public class TokenController {
    private final TokenService tokenService;

    private static final String ISSUE_NEW_ACCESS_TOKEN = "리프레시 토큰을 통해 새로운 엑세스 토큰 발급";
    private static final String ISSUE_NEW_ACCESS_TOKEN_DESCRIPTION
            = "리프레시 토큰을 입력해 새로운 엑세스 토큰을 발급 받을 수 있습니다.";
    private static final String REFRESH_TOKEN = "리프레시 토큰";
    private static final String REFRESH_TOKEN_EXAMPLE = "refreshTokenValue";

    @ApiOperation(value = ISSUE_NEW_ACCESS_TOKEN, notes = ISSUE_NEW_ACCESS_TOKEN_DESCRIPTION)
    @PostMapping()
    public ResponseEntity<String> issueNewAccessToken(@RequestParam(name = "refreshToken")
                                                      @ApiParam(value = REFRESH_TOKEN, example = REFRESH_TOKEN_EXAMPLE)
                                                      String refreshToken) {
        String newAccessToken = tokenService.issueNewAccessToken(refreshToken);

        return ResponseEntity.status(HttpStatus.CREATED).body(newAccessToken);
    }
}
