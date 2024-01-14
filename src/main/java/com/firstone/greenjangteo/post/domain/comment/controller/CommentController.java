package com.firstone.greenjangteo.post.domain.comment.controller;

import com.firstone.greenjangteo.post.domain.comment.dto.CommentRequestDto;
import com.firstone.greenjangteo.post.domain.comment.dto.CommentResponseDto;
import com.firstone.greenjangteo.post.domain.comment.model.entity.Comment;
import com.firstone.greenjangteo.post.domain.comment.service.CommentService;
import com.firstone.greenjangteo.utility.FormatConverter;
import com.firstone.greenjangteo.utility.InputFormatValidator;
import com.firstone.greenjangteo.utility.RoleValidator;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static com.firstone.greenjangteo.post.controller.PostController.POST_ID;
import static com.firstone.greenjangteo.utility.PagingConstant.*;
import static com.firstone.greenjangteo.web.ApiConstant.ID_EXAMPLE;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    private static final String CREATE_COMMENT = "댓글 등록";
    private static final String CREATE_COMMENT_DESCRIPTION = "회원 ID와 게시글 ID, 댓글 내용을 입력해 댓글을 등록할 수 있습니다.";
    private static final String CREATE_COMMENT_FORM = "댓글 등록 양식";

    private static final String GET_ALL_COMMENTS = "게시글 전체 댓글 목록 조회";
    private static final String GET_ALL_COMMENTS_DESCRIPTION = "게시글 전체 댓글 목록을 조회할 수 있습니다." +
            "\n페이징 옵션을 선택할 수 있습니다.";

    private static final String UPDATE_COMMENT = "댓글 수정";
    private static final String UPDATE_COMMENT_DESCRIPTION = "댓글 ID와 회원 ID를 입력해 댓글을 수정할 수 있습니다.";
    private static final String UPDATE_COMMENT_FORM = "댓글 수정 양식";
    public static final String COMMENT_ID = "댓글 ID";
    
    @ApiOperation(value = CREATE_COMMENT, notes = CREATE_COMMENT_DESCRIPTION)
    @PostMapping()
    public ResponseEntity<CommentResponseDto> createComment
            (@Valid @RequestBody @ApiParam(value = CREATE_COMMENT_FORM) CommentRequestDto commentRequestDto) {
        InputFormatValidator.validateId(commentRequestDto.getPostId());
        RoleValidator.checkAdminOrPrincipalAuthentication(commentRequestDto.getUserId());

        Comment comment = commentService.createComment(commentRequestDto);

        return buildResponse(CommentResponseDto.from(comment));
    }

    @ApiOperation(value = GET_ALL_COMMENTS, notes = GET_ALL_COMMENTS_DESCRIPTION)
    @GetMapping()
    public ResponseEntity<List<CommentResponseDto>> getAllComments
            (@RequestParam(name = "postId")
             @ApiParam(value = POST_ID, example = ID_EXAMPLE) String postId,
             @RequestParam(defaultValue = TRUE)
             @ApiParam(value = IS_PAGINATION_USED, example = TRUE) boolean paged,
             @RequestParam(defaultValue = ZERO)
             @ApiParam(value = CURRENT_PAGE_NUMBER, example = ZERO) int page,
             @RequestParam(defaultValue = FIVE)
             @ApiParam(value = NUMBER_OF_ITEMS_PER_PAGE, example = FIVE) int size,
             @RequestParam(defaultValue = ORDER_BY_ID_ASCENDING)
             @ApiParam(value = SORTING_METHOD, example = ORDER_BY_CREATED_AT_DESCENDING) String sort) {
        InputFormatValidator.validateId(postId);
        Pageable pageable = paged
                ? PageRequest.of(page, size, FormatConverter.parseSortString(sort))
                : Pageable.unpaged();

        List<Comment> comments = commentService.getComments(pageable, Long.parseLong(postId)).getContent();
        List<CommentResponseDto> commentResponseDtos
                = comments.stream().map(CommentResponseDto::from).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(commentResponseDtos);
    }

    @ApiOperation(value = UPDATE_COMMENT, notes = UPDATE_COMMENT_DESCRIPTION)
    @PutMapping("{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment
            (@PathVariable @ApiParam(value = COMMENT_ID, example = ID_EXAMPLE) String commentId,
             @Valid @RequestBody @ApiParam(value = UPDATE_COMMENT_FORM) CommentRequestDto commentRequestDto) {
        String userId = commentRequestDto.getUserId();

        InputFormatValidator.validateId(commentId);
        InputFormatValidator.validateId(userId);

        RoleValidator.checkAdminOrPrincipalAuthentication(userId);

        Comment comment = commentService.updateComment(Long.parseLong(commentId), commentRequestDto);

        return ResponseEntity.status(HttpStatus.OK).body(CommentResponseDto.from(comment));
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
