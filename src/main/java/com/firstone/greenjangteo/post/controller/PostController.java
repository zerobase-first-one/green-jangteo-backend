package com.firstone.greenjangteo.post.controller;

import com.firstone.greenjangteo.post.domain.comment.service.CommentService;
import com.firstone.greenjangteo.post.domain.view.model.entity.View;
import com.firstone.greenjangteo.post.domain.view.model.service.ViewService;
import com.firstone.greenjangteo.post.dto.PostRequestDto;
import com.firstone.greenjangteo.post.dto.PostResponseDto;
import com.firstone.greenjangteo.post.dto.PostsResponseDto;
import com.firstone.greenjangteo.post.model.entity.Post;
import com.firstone.greenjangteo.post.service.PostService;
import com.firstone.greenjangteo.user.dto.request.UserIdRequestDto;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static com.firstone.greenjangteo.utility.PagingConstant.*;
import static com.firstone.greenjangteo.web.ApiConstant.*;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final ViewService viewService;
    private final CommentService commentService;

    private static final String CREATE_POST = "게시글 등록";
    private static final String CREATE_POST_DESCRIPTION = "회원 ID와 게시글 내용을 입력해 게시글을 등록할 수 있습니다.";
    private static final String CREATE_POST_FORM = "게시글 등록 양식";

    private static final String GET_ALL_POSTS = "전체 게시글 목록 조회";
    private static final String GET_ALL_POSTS_DESCRIPTION = "전체 게시글 목록을 조회할 수 있습니다." +
            "\n페이징 옵션을 선택할 수 있습니다.";

    private static final String GET_MY_POSTS = "자신의 게시글 목록 조회";
    private static final String GET_MY_POSTS_DESCRIPTION = "자신의 게시글 목록을 조회할 수 있습니다." +
            "\n페이징 옵션을 선택할 수 있습니다.";

    private static final String GET_POST = "게시글 조회";
    private static final String GET_POST_DESCRIPTION = "게시글 ID와 게시자 ID를 입력해 게시글을 조회할 수 있습니다.";
    public static final String POST_ID = "게시글 ID";
    private static final String WRITER_ID = "게시자 ID";

    private static final String UPDATE_POST = "게시글 수정";
    private static final String UPDATE_POST_DESCRIPTION = "게시글 ID와 회원 ID를 입력해 게시글을 수정할 수 있습니다.";
    private static final String UPDATE_POST_FORM = "게시글 수정 양식";

    private static final String DELETE_POST = "게시글 삭제";
    private static final String DELETE_POST_DESCRIPTION = "게시글 ID와 회원 ID를 입력해 게시글을 삭제할 수 있습니다.";

    @ApiOperation(value = CREATE_POST, notes = CREATE_POST_DESCRIPTION)
    @PostMapping
    public ResponseEntity<PostResponseDto> createPost
            (@Valid @RequestBody @ApiParam(value = CREATE_POST_FORM) PostRequestDto postRequestDto) {
        InputFormatValidator.validateId(postRequestDto.getUserId());
        RoleValidator.checkAdminOrPrincipalAuthentication(postRequestDto.getUserId());

        Post post = postService.createPost(postRequestDto);

        return buildResponse(PostResponseDto.from(post));
    }

    @ApiOperation(value = GET_ALL_POSTS, notes = GET_ALL_POSTS_DESCRIPTION)
    @GetMapping()
    public ResponseEntity<List<PostsResponseDto>> getAllPosts
            (@RequestParam(defaultValue = TRUE)
             @ApiParam(value = IS_PAGINATION_USED, example = TRUE) boolean paged,
             @RequestParam(defaultValue = ZERO)
             @ApiParam(value = CURRENT_PAGE_NUMBER, example = ZERO) int page,
             @RequestParam(defaultValue = FIVE)
             @ApiParam(value = NUMBER_OF_ITEMS_PER_PAGE, example = FIVE) int size,
             @RequestParam(defaultValue = ORDER_BY_ID_ASCENDING)
             @ApiParam(value = SORTING_METHOD, example = ORDER_BY_CREATED_AT_DESCENDING) String sort) {
        Pageable pageable = paged
                ? PageRequest.of(page, size, FormatConverter.parseSortString(sort))
                : Pageable.unpaged();

        List<Post> posts = postService.getPosts(pageable).getContent();

        List<PostsResponseDto> postsResponseDtos = convertPostsToPostsResponseDtos(posts);

        return ResponseEntity.status(HttpStatus.OK).body(postsResponseDtos);
    }

    @ApiOperation(value = GET_MY_POSTS, notes = GET_MY_POSTS_DESCRIPTION)
    @PreAuthorize(PRINCIPAL_POINTCUT)
    @GetMapping("/my")
    public ResponseEntity<List<PostsResponseDto>> getMyPosts
            (@RequestParam(name = "userId")
             @ApiParam(value = USER_ID_VALUE, example = ID_EXAMPLE) String userId,
             @RequestParam(defaultValue = TRUE)
             @ApiParam(value = IS_PAGINATION_USED, example = TRUE) boolean paged,
             @RequestParam(defaultValue = ZERO)
             @ApiParam(value = CURRENT_PAGE_NUMBER, example = ZERO) int page,
             @RequestParam(defaultValue = FIVE)
             @ApiParam(value = NUMBER_OF_ITEMS_PER_PAGE, example = FIVE) int size,
             @RequestParam(defaultValue = ORDER_BY_ID_ASCENDING)
             @ApiParam(value = SORTING_METHOD, example = ORDER_BY_CREATED_AT_DESCENDING) String sort) {
        InputFormatValidator.validateId(userId);
        Pageable pageable = paged
                ? PageRequest.of(page, size, FormatConverter.parseSortString(sort))
                : Pageable.unpaged();

        List<Post> posts = postService.getPosts(pageable, Long.parseLong(userId)).getContent();

        List<PostsResponseDto> postsResponseDtos = convertPostsToPostsResponseDtos(posts);

        return ResponseEntity.status(HttpStatus.OK).body(postsResponseDtos);
    }

    @ApiOperation(value = GET_POST, notes = GET_POST_DESCRIPTION)
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDto> getPost
            (@PathVariable("postId") @ApiParam(value = POST_ID, example = ID_EXAMPLE) String postId,
             @RequestParam(name = "writerId") @ApiParam(value = WRITER_ID, example = ID_EXAMPLE) String writerId) {
        InputFormatValidator.validateId(postId);
        InputFormatValidator.validateId(writerId);

        Post post = postService.getPost(Long.parseLong(postId), Long.parseLong(writerId));
        View view = viewService.addAndGetView(post.getId());
        int commentCount = commentService.getCommentCountForPost(post.getId());

        return ResponseEntity.status(HttpStatus.OK).body(PostResponseDto.from(post, view.getViewCount(), commentCount));
    }

    @ApiOperation(value = UPDATE_POST, notes = UPDATE_POST_DESCRIPTION)
    @PutMapping("{postId}")
    public ResponseEntity<PostResponseDto> updatePost
            (@PathVariable @ApiParam(value = POST_ID, example = ID_EXAMPLE) String postId,
             @Valid @RequestBody @ApiParam(value = UPDATE_POST_FORM) PostRequestDto postRequestDto) {
        String userId = postRequestDto.getUserId();

        InputFormatValidator.validateId(postId);
        InputFormatValidator.validateId(userId);

        RoleValidator.checkAdminOrPrincipalAuthentication(userId);

        Post post = postService.updatePost(Long.parseLong(postId), postRequestDto);
        View view = viewService.getView(post.getId());
        int commentCount = commentService.getCommentCountForPost(post.getId());

        return ResponseEntity.status(HttpStatus.OK).body(PostResponseDto.from(post, view.getViewCount(), commentCount));
    }

    @ApiOperation(value = DELETE_POST, notes = DELETE_POST_DESCRIPTION)
    @DeleteMapping("{postId}")
    public ResponseEntity<PostResponseDto> deletePost
            (@PathVariable @ApiParam(value = POST_ID, example = ID_EXAMPLE) String postId,
             @RequestBody @ApiParam(value = USER_ID_VALUE) UserIdRequestDto userIdRequestDto) {
        String userId = userIdRequestDto.getUserId();
        InputFormatValidator.validateId(postId);
        InputFormatValidator.validateId(userId);

        RoleValidator.checkAdminOrPrincipalAuthentication(userId);

        postService.deletePost(Long.parseLong(postId), Long.parseLong(userId));

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
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

    private List<PostsResponseDto> convertPostsToPostsResponseDtos(List<Post> posts) {
        List<PostsResponseDto> postsResponseDtos = new ArrayList<>();
        for (Post post : posts) {
            View view = viewService.getView(post.getId());
            int commentCount = commentService.getCommentCountForPost(post.getId());
            PostsResponseDto postsResponseDto = PostsResponseDto.from(post, view.getViewCount(), commentCount);
            postsResponseDtos.add(postsResponseDto);
        }

        return postsResponseDtos;
    }
}
