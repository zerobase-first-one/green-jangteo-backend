package com.firstone.greenjangteo.post.domain.comment.repository;

import com.firstone.greenjangteo.post.domain.comment.model.entity.Comment;
import com.firstone.greenjangteo.post.domain.comment.service.CommentTestObjectFactory;
import com.firstone.greenjangteo.post.domain.image.model.entity.Image;
import com.firstone.greenjangteo.post.domain.image.testutil.ImageTestObjectFactory;
import com.firstone.greenjangteo.post.model.entity.Post;
import com.firstone.greenjangteo.post.repository.PostRepository;
import com.firstone.greenjangteo.post.utility.PostTestObjectFactory;
import com.firstone.greenjangteo.user.model.entity.User;
import com.firstone.greenjangteo.user.repository.UserRepository;
import com.firstone.greenjangteo.user.testutil.UserTestObjectFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.firstone.greenjangteo.post.domain.image.testutil.ImageTestConstant.*;
import static com.firstone.greenjangteo.post.utility.PostTestConstant.*;
import static com.firstone.greenjangteo.user.model.Role.ROLE_BUYER;
import static com.firstone.greenjangteo.user.testutil.UserTestConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @DisplayName("게시글의 모든 댓글 목록을 페이징 처리해 생성 순서 내림차순으로 검색할 수 있다.")
    @Test
    void findByPostIdWithPaging() {
        // given
        Post post1 = PostTestObjectFactory.createPost(SUBJECT1, CONTENT1);
        Post post2 = PostTestObjectFactory.createPost(SUBJECT2, CONTENT2);
        postRepository.saveAll(List.of(post1, post2));

        User user = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.name())
        );
        userRepository.save(user);

        List<Image> images = ImageTestObjectFactory.createImages();

        Comment comment1 = CommentTestObjectFactory.createComment(CONTENT1, user, post1, images);
        Comment comment2 = CommentTestObjectFactory.createComment(CONTENT1, user, post2, images);
        Comment comment3 = CommentTestObjectFactory.createComment(CONTENT2, user, post1, images);
        Comment comment4 = CommentTestObjectFactory.createComment(CONTENT3, user, post1, images);

        commentRepository.saveAll(List.of(comment1, comment2, comment3, comment4));

        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(0, 2, sort);

        // when
        List<Comment> comments = commentRepository.findByPostId(post1.getId(), pageable).getContent();

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
}