package com.firstone.greenjangteo.post.domain.comment.controller;

import com.firstone.greenjangteo.post.domain.comment.dto.CommentRequestDto;
import com.firstone.greenjangteo.post.domain.comment.dto.CommentResponseDto;
import com.firstone.greenjangteo.post.domain.comment.model.entity.Comment;
import com.firstone.greenjangteo.post.domain.comment.service.CommentService;
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
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    private static final String CREATE_COMMENT = "댓글 등록";
    private static final String CREATE_COMMENT_DESCRIPTION = "회원 ID와 게시글 ID, 댓글 내용을 입력해 댓글을 등록할 수 있습니다.";
    private static final String CREATE_COMMENT_FORM = "댓글 등록 양식";

    @ApiOperation(value = CREATE_COMMENT, notes = CREATE_COMMENT_DESCRIPTION)
    @PostMapping()
    public ResponseEntity<CommentResponseDto> createComment
            (@Valid @RequestBody @ApiParam(value = CREATE_COMMENT_FORM) CommentRequestDto commentRequestDto) {
        InputFormatValidator.validateId(commentRequestDto.getPostId());
        RoleValidator.checkAdminOrPrincipalAuthentication(commentRequestDto.getUserId());

        Comment comment = commentService.createComment(commentRequestDto);

        return buildResponse(CommentResponseDto.from(comment));
    }

    private ResponseEntity<CommentResponseDto> buildResponse(CommentResponseDto commentResponseDto) {
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{commentId}")
                .buildAndExpand(commentResponseDto.getCommentId())
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);

        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(commentResponseDto);
    }
}
