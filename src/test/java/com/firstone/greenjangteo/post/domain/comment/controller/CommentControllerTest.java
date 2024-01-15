package com.firstone.greenjangteo.post.domain.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstone.greenjangteo.post.domain.comment.dto.CommentRequestDto;
import com.firstone.greenjangteo.post.domain.comment.model.entity.Comment;
import com.firstone.greenjangteo.post.domain.comment.service.CommentService;
import com.firstone.greenjangteo.post.domain.comment.service.CommentTestObjectFactory;
import com.firstone.greenjangteo.post.domain.image.dto.ImageRequestDto;
import com.firstone.greenjangteo.post.domain.image.model.entity.Image;
import com.firstone.greenjangteo.post.domain.image.testutil.ImageTestObjectFactory;
import com.firstone.greenjangteo.post.model.entity.Post;
import com.firstone.greenjangteo.user.dto.request.UserIdRequestDto;
import com.firstone.greenjangteo.user.model.Username;
import com.firstone.greenjangteo.user.model.entity.User;
import com.firstone.greenjangteo.user.security.CustomAuthenticationEntryPoint;
import com.firstone.greenjangteo.user.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.firstone.greenjangteo.order.testutil.OrderTestConstant.BUYER_ID;
import static com.firstone.greenjangteo.post.utility.PostTestConstant.*;
import static com.firstone.greenjangteo.user.testutil.UserTestConstant.USERNAME1;
import static com.firstone.greenjangteo.utility.PagingConstant.*;
import static com.firstone.greenjangteo.web.ApiConstant.ID_EXAMPLE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(controllers = CommentController.class)
class CommentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @DisplayName("등록된 게시글의 댓글을 등록할 수 있다.")
    @Test
    @WithMockUser(username = BUYER_ID, roles = {"BUYER"})
    void createComment() throws Exception {
        // given
        User user = mock(User.class);
        Post post = mock(Post.class);
        List<ImageRequestDto> imageRequestDtos = ImageTestObjectFactory.createImageRequestDtos();

        CommentRequestDto commentRequestDto
                = CommentTestObjectFactory.createCommentRequestDto(BUYER_ID, ID_EXAMPLE, CONTENT1, imageRequestDtos);

        List<Image> images = ImageTestObjectFactory.createImages();
        Comment comment = CommentTestObjectFactory.createComment(ID_EXAMPLE, CONTENT1, user, post, images);
        when(commentService.createComment(any(CommentRequestDto.class))).thenReturn(comment);
        when(user.getUsername()).thenReturn(Username.of(USERNAME1));

        // when, then
        mockMvc.perform(post("/comments")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(commentRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @DisplayName("게시글의 전체 댓글 목록을 페이징 처리해 조회할 수 있다.")
    @Test
    @WithMockUser
    void getAllComments() throws Exception {
        // given
        User user = mock(User.class);
        Post post = mock(Post.class);

        Comment comment1 = CommentTestObjectFactory.createComment(CONTENT1, user, post);
        Comment comment2 = CommentTestObjectFactory.createComment(CONTENT2, user, post);
        Comment comment3 = CommentTestObjectFactory.createComment(CONTENT3, user, post);

        List<Comment> comments = List.of(comment1, comment2, comment3);

        Pageable pageable = PageRequest.of(0, 5);
        Page<Comment> commentPage = new PageImpl<>(comments, pageable, comments.size());

        when(commentService.getComments(any(Pageable.class), any(Long.class))).thenReturn(commentPage);
        when(user.getUsername()).thenReturn(Username.of(USERNAME1));

        // when, then
        mockMvc.perform(get("/comments")
                        .param("postId", ID_EXAMPLE)
                        .param("paged", TRUE)
                        .param("page", ZERO)
                        .param("size", FIVE)
                        .param("sort", ORDER_BY_CREATED_AT_DESCENDING))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("댓글 ID를 전송해 댓글을 수정할 수 있다.")
    @Test
    @WithMockUser(username = BUYER_ID, roles = {"BUYER"})
    void updateComment() throws Exception {
        // given
        User user = mock(User.class);
        Post post = mock(Post.class);
        Comment comment = CommentTestObjectFactory.createComment(ID_EXAMPLE, CONTENT1, user, post);

        List<ImageRequestDto> imageRequestDtos = ImageTestObjectFactory.createImageRequestDtos();
        CommentRequestDto commentRequestDto
                = CommentTestObjectFactory.createCommentRequestDto(BUYER_ID, ID_EXAMPLE, CONTENT2, imageRequestDtos);

        when(commentService.updateComment(anyLong(), any(CommentRequestDto.class))).thenReturn(comment);
        when(user.getUsername()).thenReturn(Username.of(USERNAME1));

        // when, then
        mockMvc.perform(put("/comments/{commentId}", comment.getId())
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(commentRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("댓글 ID와 회원 ID를 전송해 댓글을 삭제할 수 있다.")
    @Test
    @WithMockUser(username = BUYER_ID, roles = {"BUYER"})
    void deleteComment() throws Exception {
        // given
        doNothing().when(commentService).deleteComment(anyLong(), anyLong());
        UserIdRequestDto userIdRequestDto = new UserIdRequestDto(BUYER_ID);

        // when, then
        mockMvc.perform(delete("/comments/{commentId}", BUYER_ID)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(userIdRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}
