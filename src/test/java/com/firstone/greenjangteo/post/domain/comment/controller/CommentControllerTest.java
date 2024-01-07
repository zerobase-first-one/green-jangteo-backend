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
import com.firstone.greenjangteo.user.model.Username;
import com.firstone.greenjangteo.user.model.entity.User;
import com.firstone.greenjangteo.user.security.CustomAuthenticationEntryPoint;
import com.firstone.greenjangteo.user.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.firstone.greenjangteo.order.testutil.OrderTestConstant.BUYER_ID;
import static com.firstone.greenjangteo.post.utility.PostTestConstant.CONTENT1;
import static com.firstone.greenjangteo.user.testutil.UserTestConstant.USERNAME1;
import static com.firstone.greenjangteo.web.ApiConstant.ID_EXAMPLE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

}