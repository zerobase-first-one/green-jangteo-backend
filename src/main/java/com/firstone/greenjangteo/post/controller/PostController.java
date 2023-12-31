package com.firstone.greenjangteo.post.controller;

import com.firstone.greenjangteo.post.dto.PostRequestDto;
import com.firstone.greenjangteo.post.dto.PostResponseDto;
import com.firstone.greenjangteo.post.model.entity.Post;
import com.firstone.greenjangteo.post.service.PostService;
import com.firstone.greenjangteo.utility.InputFormatValidator;
import com.firstone.greenjangteo.utility.RoleValidator;
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

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    private static final String CREATE_POST = "게시물 등록";
    private static final String CREATE_POST_DESCRIPTION = "회원 ID와 게시물 내용을 입력해 게시물을 등록할 수 있습니다.";
    private static final String CREATE_POST_FORM = "게시물 등록 양식";

    @ApiOperation(value = CREATE_POST, notes = CREATE_POST_DESCRIPTION)
    @PostMapping
    public ResponseEntity<PostResponseDto> createPost
            (@Valid @RequestBody @ApiParam(value = CREATE_POST_FORM) PostRequestDto postRequestDto) {
        InputFormatValidator.validateId(postRequestDto.getUserId());
        RoleValidator.checkAdminOrPrincipalAuthentication(postRequestDto.getUserId());

        Post post = postService.createPost(postRequestDto);

        return buildResponse(PostResponseDto.from(post));
    }

    private ResponseEntity<PostResponseDto> buildResponse(PostResponseDto postResponseDto) {
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{postId}")
                .buildAndExpand(postResponseDto.getPostId())
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);

        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(postResponseDto);
    }
}
