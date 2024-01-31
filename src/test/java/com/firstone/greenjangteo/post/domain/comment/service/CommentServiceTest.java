package com.firstone.greenjangteo.post.domain.comment.service;

import com.firstone.greenjangteo.post.domain.comment.dto.CommentRequestDto;
import com.firstone.greenjangteo.post.domain.comment.exception.serious.InconsistentCommentException;
import com.firstone.greenjangteo.post.domain.comment.model.entity.Comment;
import com.firstone.greenjangteo.post.domain.comment.repository.CommentRepository;
import com.firstone.greenjangteo.post.domain.image.dto.ImageRequestDto;
import com.firstone.greenjangteo.post.domain.image.model.entity.Image;
import com.firstone.greenjangteo.post.domain.image.repository.ImageRepository;
import com.firstone.greenjangteo.post.domain.image.testutil.ImageTestObjectFactory;
import com.firstone.greenjangteo.post.model.entity.Post;
import com.firstone.greenjangteo.post.repository.PostRepository;
import com.firstone.greenjangteo.post.service.PostService;
import com.firstone.greenjangteo.post.utility.PostTestObjectFactory;
import com.firstone.greenjangteo.user.model.entity.User;
import com.firstone.greenjangteo.user.repository.UserRepository;
import com.firstone.greenjangteo.user.testutil.UserTestObjectFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import java.util.List;

import static com.firstone.greenjangteo.post.domain.comment.exception.message.InconsistentExceptionMessage.INCONSISTENT_COMMENT_EXCEPTION_COMMENT_ID;
import static com.firstone.greenjangteo.post.domain.comment.exception.message.InconsistentExceptionMessage.INCONSISTENT_COMMENT_EXCEPTION_POST_ID;
import static com.firstone.greenjangteo.post.domain.comment.exception.message.NotFoundExceptionMessage.COMMENTED_USER_ID_NOT_FOUND_EXCEPTION;
import static com.firstone.greenjangteo.post.domain.comment.exception.message.NotFoundExceptionMessage.COMMENT_NOT_FOUND_EXCEPTION;
import static com.firstone.greenjangteo.post.domain.image.testutil.ImageTestConstant.*;
import static com.firstone.greenjangteo.post.utility.PostTestConstant.*;
import static com.firstone.greenjangteo.user.model.Role.ROLE_BUYER;
import static com.firstone.greenjangteo.user.testutil.UserTestConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class CommentServiceTest {
    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EntityManager entityManager;

    @DisplayName("게시글의 댓글을 등록할 수 있다.")
    @Test
    void createComment() {
        // given
        User user = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.name())
        );
        userRepository.save(user);

        Post post = PostTestObjectFactory.createPost(SUBJECT1, CONTENT1);
        postRepository.save(post);

        List<ImageRequestDto> imageRequestDtos = ImageTestObjectFactory.createImageRequestDtos();
        CommentRequestDto commentRequestDto = CommentTestObjectFactory
                .createCommentRequestDto(user.getId().toString(), post.getId().toString(), CONTENT2, imageRequestDtos);

        // when
        Comment comment = commentService.createComment(commentRequestDto);

        // then
        assertThat(comment.getContent()).isEqualTo(CONTENT2);
        assertThat(comment.getPost()).isEqualTo(post);
        assertThat(comment.getUser()).isEqualTo(user);
        assertThat(comment.getImages()).hasSize(imageRequestDtos.size())
                .extracting("url", "positionInContent")
                .containsExactlyInAnyOrder(
                        tuple(IMAGE_URL1, POSITION_IN_CONTENT),
                        tuple(IMAGE_URL2, POSITION_IN_CONTENT + 1),
                        tuple(IMAGE_URL3, POSITION_IN_CONTENT + 2)
                );
    }

    @DisplayName("게시글의 모든 댓글 목록을 페이징 처리해 댓글 생성 순서 내림차순으로 조회할 수 있다.")
    @Test
    void getComments() {
        // given
        Post post1 = PostTestObjectFactory.createPost(SUBJECT1, CONTENT1);
        Post post2 = PostTestObjectFactory.createPost(SUBJECT2, CONTENT2);
        postRepository.saveAll(List.of(post1, post2));

        User user = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.name())
        );
        userRepository.save(user);

        List<Image> images = ImageTestObjectFactory.createImages();
        imageRepository.saveAll(images);

        Comment comment1 = CommentTestObjectFactory.createComment(CONTENT1, user, post1, images);
        Comment comment2 = CommentTestObjectFactory.createComment(CONTENT1, user, post2, images);
        Comment comment3 = CommentTestObjectFactory.createComment(CONTENT2, user, post1, images);
        Comment comment4 = CommentTestObjectFactory.createComment(CONTENT3, user, post1, images);

        commentRepository.saveAll(List.of(comment1, comment2, comment3, comment4));

        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(0, 2, sort);

        // when
        List<Comment> comments = commentService.getComments(pageable, post1.getId()).getContent();

        // then
        assertThat(comments).hasSize(2)
                .extracting("content", "user", "post")
                .containsExactlyInAnyOrder(
                        tuple(CONTENT2, user, post1),
                        tuple(CONTENT3, user, post1)
                );
        assertThat(comments.get(0).getImages()).hasSize(3)
                .extracting("url", "positionInContent")
                .containsExactlyInAnyOrder(
                        tuple(IMAGE_URL1, POSITION_IN_CONTENT),
                        tuple(IMAGE_URL2, POSITION_IN_CONTENT + 1),
                        tuple(IMAGE_URL3, POSITION_IN_CONTENT + 2)
                );
        assertThat(comments.get(1).getImages()).hasSize(3)
                .extracting("url", "positionInContent")
                .containsExactlyInAnyOrder(
                        tuple(IMAGE_URL1, POSITION_IN_CONTENT),
                        tuple(IMAGE_URL2, POSITION_IN_CONTENT + 1),
                        tuple(IMAGE_URL3, POSITION_IN_CONTENT + 2)
                );
    }

    @DisplayName("댓글 ID와 작성자 ID를 통해 댓글을 조회할 수 있다.")
    @Test
    void getComment() {
        // given
        Post post = PostTestObjectFactory.createPost(SUBJECT1, CONTENT1);
        postRepository.save(post);

        User user = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.name())
        );
        userRepository.save(user);

        List<Image> images = ImageTestObjectFactory.createImages();
        imageRepository.saveAll(images);

        Comment createdComment = CommentTestObjectFactory.createComment(CONTENT1, user, post, images);
        commentRepository.save(createdComment);

        // when
        Comment foundComment = commentService.getComment(createdComment.getId(), user.getId());

        // then
        assertThat(foundComment).isEqualTo(createdComment);
        assertThat(foundComment.getPost()).isEqualTo(post);
        assertThat(foundComment.getUser()).isEqualTo(user);
        assertThat(foundComment.getImages()).hasSize(images.size())
                .extracting("url", "positionInContent")
                .containsExactlyInAnyOrder(
                        tuple(IMAGE_URL1, POSITION_IN_CONTENT),
                        tuple(IMAGE_URL2, POSITION_IN_CONTENT + 1),
                        tuple(IMAGE_URL3, POSITION_IN_CONTENT + 2)
                );
    }

    @DisplayName("일치하지 않는 댓글 ID 또는 작성자 ID를 통해 댓글을 조회하려 하면 EntityNotFoundException이 발생한다.")
    @Test
    void getCommentWithWrongCommentOrWriterId() {
        // given
        Post post = PostTestObjectFactory.createPost(SUBJECT1, CONTENT1);
        postRepository.save(post);

        User user = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.name())
        );
        userRepository.save(user);

        Comment createdComment = CommentTestObjectFactory.createComment(CONTENT1, user, post);
        commentRepository.save(createdComment);

        // when, then
        assertThatThrownBy(() -> commentService.getComment(createdComment.getId() + 1, user.getId()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(COMMENT_NOT_FOUND_EXCEPTION + (createdComment.getId() + 1)
                        + COMMENTED_USER_ID_NOT_FOUND_EXCEPTION + user.getId());

        assertThatThrownBy(() -> commentService.getComment(createdComment.getId(), user.getId() + 1))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(COMMENT_NOT_FOUND_EXCEPTION + createdComment.getId()
                        + COMMENTED_USER_ID_NOT_FOUND_EXCEPTION + (user.getId() + 1));
    }

    @DisplayName("게시글 ID를 통해 게시글의 댓글 수를 조회할 수 있다.")
    @Test
    void getCommentCountForPost() {
        // given
        Post post1 = PostTestObjectFactory.createPost(SUBJECT1, CONTENT1);
        Post post2 = PostTestObjectFactory.createPost(SUBJECT2, CONTENT2);
        postRepository.saveAll(List.of(post1, post2));

        Comment comment1 = CommentTestObjectFactory.createComment(CONTENT1, post1);
        Comment comment2 = CommentTestObjectFactory.createComment(CONTENT2, post2);
        Comment comment3 = CommentTestObjectFactory.createComment(CONTENT3, post2);
        commentRepository.saveAll(List.of(comment1, comment2, comment3));

        // when
        int commentCount1 = commentService.getCommentCountForPost(post1.getId());
        int commentCount2 = commentService.getCommentCountForPost(post2.getId());

        // then
        assertThat(commentCount1).isEqualTo(1);
        assertThat(commentCount2).isEqualTo(2);
    }

    @DisplayName("댓글 ID를 전송해 댓글 내용을 수정할 수 있다.")
    @Test
    void updateComment() {
        // given
        User user = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.name())
        );
        userRepository.save(user);

        Post post = PostTestObjectFactory.createPost(SUBJECT1, CONTENT1);
        postRepository.save(post);

        Comment createdComment = CommentTestObjectFactory.createComment(CONTENT1, user, post);
        commentRepository.save(createdComment);

        List<Image> images = ImageTestObjectFactory.createImages(createdComment);
        imageRepository.saveAll(images);

        List<ImageRequestDto> imageRequestDtos = ImageTestObjectFactory.createImageUpdateRequestDtos();

        CommentRequestDto commentRequestDto = CommentTestObjectFactory.createCommentRequestDto(
                user.getId().toString(), post.getId().toString(), CONTENT2, imageRequestDtos
        );

        // when
        Comment savedComment = commentService.updateComment(createdComment.getId(), commentRequestDto);
        Comment foundComment = commentRepository.findById(savedComment.getId()).get();

        // then
        assertThat(savedComment).isEqualTo(foundComment);
        assertThat(savedComment.getContent()).isEqualTo(CONTENT2);
        assertThat(savedComment.getPost()).isEqualTo(post);
        assertThat(savedComment.getUser()).isEqualTo(user);
        assertThat(savedComment.getModifiedAt()).isNotEqualTo(savedComment.getCreatedAt());
        assertThat(savedComment.getImages()).hasSize(images.size())
                .extracting("url", "positionInContent")
                .containsExactlyInAnyOrder(
                        tuple(IMAGE_URL3, POSITION_IN_CONTENT),
                        tuple(IMAGE_URL2, POSITION_IN_CONTENT + 2),
                        tuple(IMAGE_URL1, POSITION_IN_CONTENT + 3)
                );
    }

    @DisplayName("존재하지 않는 댓글 ID 또는 작성자 ID를 전송해 댓글을 수정하려 하면 EntityNotFoundException이 발생한다.")
    @Test
    void updateCommentWithWrongCommentOrWriterId() {
        // given
        User user = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.name())
        );
        userRepository.save(user);

        Post post = PostTestObjectFactory.createPost(SUBJECT1, CONTENT1);
        postRepository.save(post);

        Comment createdComment = CommentTestObjectFactory.createComment(CONTENT1, user, post);
        commentRepository.save(createdComment);

        List<Image> images = ImageTestObjectFactory.createImages(createdComment);
        imageRepository.saveAll(images);

        List<ImageRequestDto> imageRequestDtos = ImageTestObjectFactory.createImageUpdateRequestDtos();

        CommentRequestDto commentRequestDto1 = CommentTestObjectFactory.createCommentRequestDto(
                user.getId().toString(), post.getId().toString(), CONTENT2, imageRequestDtos
        );

        Long requestedWriterId = user.getId() + 1;
        CommentRequestDto commentRequestDto2 = CommentTestObjectFactory.createCommentRequestDto(
                requestedWriterId.toString(), post.getId().toString(), CONTENT2, imageRequestDtos
        );

        // when, then
        assertThatThrownBy(() -> commentService.updateComment(createdComment.getId() + 1, commentRequestDto1))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(
                        COMMENT_NOT_FOUND_EXCEPTION + (createdComment.getId() + 1)
                                + COMMENTED_USER_ID_NOT_FOUND_EXCEPTION + user.getId()
                );

        assertThatThrownBy(() -> commentService.updateComment(createdComment.getId(), commentRequestDto2))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(
                        COMMENT_NOT_FOUND_EXCEPTION + (createdComment.getId())
                                + COMMENTED_USER_ID_NOT_FOUND_EXCEPTION + requestedWriterId
                );
    }

    @DisplayName("일치하지 않는 게시글 ID를 전송해 댓글을 수정하려 하면 InconsistentCommentException이 발생한다.")
    @Test
    void updateCommentWithInconsistentPostId() {
        // given
        User user = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.name())
        );
        userRepository.save(user);

        Post post = PostTestObjectFactory.createPost(SUBJECT1, CONTENT1);
        postRepository.save(post);

        Comment createdComment = CommentTestObjectFactory.createComment(CONTENT1, user, post);
        commentRepository.save(createdComment);

        List<Image> images = ImageTestObjectFactory.createImages(createdComment);
        imageRepository.saveAll(images);

        List<ImageRequestDto> imageRequestDtos = ImageTestObjectFactory.createImageUpdateRequestDtos();

        Long requestedPostId = post.getId() + 1;
        CommentRequestDto commentRequestDto = CommentTestObjectFactory.createCommentRequestDto(
                user.getId().toString(), requestedPostId.toString(), CONTENT2, imageRequestDtos
        );

        // when, then
        assertThatThrownBy(() -> commentService.updateComment(createdComment.getId(), commentRequestDto))
                .isInstanceOf(InconsistentCommentException.class)
                .hasMessage(
                        INCONSISTENT_COMMENT_EXCEPTION_POST_ID + requestedPostId
                                + INCONSISTENT_COMMENT_EXCEPTION_COMMENT_ID + createdComment.getId()
                );
    }

    @DisplayName("댓글 ID와 회원 ID를 전송해 댓글을 삭제할 수 있다.")
    @ParameterizedTest
    @CsvSource({
            "안녕하세요?",
            "가나다라abc마바 123454321 aBc 가나다 ab 123",
            "12345"
    })
    void deleteComment(String content) {
        // given
        User user = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.name())
        );
        userRepository.save(user);

        Comment comment = CommentTestObjectFactory.createComment(content, user);
        commentRepository.save(comment);

        List<Image> images = ImageTestObjectFactory.createImages(comment);
        imageRepository.saveAll(images);

        entityManager.refresh(comment);

        Long commentId = comment.getId();

        // when
        commentService.deleteComment(commentId, user.getId());

        // then
        assertThat(postRepository.findById(commentId)).isNotPresent();
        assertThat(imageRepository.findAllByCommentIdOrderByIdAsc(commentId)).isEmpty();
    }

    @DisplayName("일치하지 않는 댓글 ID 또는 회원 ID를 전송해 댓글을 삭제하려 하면 EntityNotFoundException이 발생한다.")
    @Test
    void deletePostWithWrongCommentOrUserId() {
        // given
        User user = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.name())
        );
        userRepository.save(user);

        Comment comment = CommentTestObjectFactory.createComment(CONTENT1, user);
        commentRepository.save(comment);

        Long commentId = comment.getId();
        Long userId = user.getId();

        // when, then
        assertThatThrownBy(() -> commentService.deleteComment(commentId + 1, userId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(
                        COMMENT_NOT_FOUND_EXCEPTION + (commentId + 1) + COMMENTED_USER_ID_NOT_FOUND_EXCEPTION + userId
                );
        assertThatThrownBy(() -> commentService.deleteComment(commentId, userId + 1))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(
                        COMMENT_NOT_FOUND_EXCEPTION + commentId + COMMENTED_USER_ID_NOT_FOUND_EXCEPTION + (userId + 1)
                );
        assertThatThrownBy(() -> commentService.deleteComment(commentId + 1, userId + 1))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(
                        COMMENT_NOT_FOUND_EXCEPTION + (commentId + 1)
                                + COMMENTED_USER_ID_NOT_FOUND_EXCEPTION + (userId + 1)
                );
    }
}
