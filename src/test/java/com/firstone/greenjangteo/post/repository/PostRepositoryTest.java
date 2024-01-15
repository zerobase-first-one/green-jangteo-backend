package com.firstone.greenjangteo.post.repository;

import com.firstone.greenjangteo.post.domain.image.model.entity.Image;
import com.firstone.greenjangteo.post.domain.image.repository.ImageRepository;
import com.firstone.greenjangteo.post.domain.image.testutil.ImageTestObjectFactory;
import com.firstone.greenjangteo.post.model.entity.Post;
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

import javax.persistence.EntityManager;
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
class PostRepositoryTest {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EntityManager entityManager;

    @DisplayName("게시글 ID와 회원 ID를 통해 게시글을 검색할 수 있다.")
    @Test
    void findByIdAndUserId() {
        // given
        User user = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.name())
        );
        userRepository.save(user);

        Post createdPost = PostTestObjectFactory.createPost(SUBJECT1, CONTENT1, user);
        postRepository.save(createdPost);

        List<Image> images = ImageTestObjectFactory.createImages(createdPost);
        imageRepository.saveAll(images);

        entityManager.refresh(createdPost);

        // when
        Post foundPost = postRepository.findByIdAndUserId(createdPost.getId(), user.getId()).get();

        // then
        assertThat(foundPost.getId()).isEqualTo(createdPost.getId());
        assertThat(foundPost.getUser().getId()).isEqualTo(createdPost.getUser().getId());
        assertThat(foundPost.getSubject()).isEqualTo(createdPost.getSubject());
        assertThat(foundPost.getContent()).isEqualTo(createdPost.getContent());
        assertThat(foundPost.getSubject()).isEqualTo(createdPost.getSubject());
        assertThat(foundPost.getContent()).isEqualTo(createdPost.getContent());

        assertThat(foundPost.getImages()).hasSize(3)
                .extracting("url", "positionInContent")
                .containsExactlyInAnyOrder(
                        tuple(IMAGE_URL1, POSITION_IN_CONTENT),
                        tuple(IMAGE_URL2, POSITION_IN_CONTENT + 1),
                        tuple(IMAGE_URL3, POSITION_IN_CONTENT + 2)
                );
    }

    @DisplayName("모든 게시글 목록을 페이징 처리해 생성 순서 내림차순으로 검색할 수 있다.")
    @Test
    void findAllWithPaging() {
        // given
        Post post1 = PostTestObjectFactory.createPost(SUBJECT1, CONTENT1);
        Post post2 = PostTestObjectFactory.createPost(SUBJECT2, CONTENT2);
        Post post3 = PostTestObjectFactory.createPost(SUBJECT3, CONTENT3);

        postRepository.saveAll(List.of(post1, post2, post3));

        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(0, 2, sort);

        // when
        List<Post> posts = postRepository.findAll(pageable).getContent();

        // then
        assertThat(posts).hasSize(2)
                .extracting("subject", "content")
                .containsExactly(tuple(SUBJECT3, CONTENT3), tuple(SUBJECT2, CONTENT2));
    }

    @DisplayName("자신의 게시글 목록을 페이징 처리해 생성 순서 내림차순으로 검색할 수 있다.")
    @Test
    void findByUserIdWithPaging() {
        // given
        User user = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.name())
        );
        userRepository.save(user);

        Post post1 = PostTestObjectFactory.createPost(SUBJECT1, CONTENT1, user);
        Post post2 = PostTestObjectFactory.createPost(SUBJECT2, CONTENT2);
        Post post3 = PostTestObjectFactory.createPost(SUBJECT3, CONTENT3, user);

        postRepository.saveAll(List.of(post1, post2, post3));

        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(0, 1, sort);

        // when
        List<Post> posts = postRepository.findByUserId(user.getId(), pageable).getContent();

        // then
        assertThat(posts).hasSize(1)
                .extracting("subject", "content")
                .containsExactly(tuple(SUBJECT3, CONTENT3));
    }

    @DisplayName("게시글 ID와 회원 ID를 통해 게시글의 존재 여부를 확인할 수 있다.")
    @Test
    void existsByIdAndUserId() {
        // given
        User user = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.name())
        );
        userRepository.save(user);

        Post post1 = PostTestObjectFactory.createPost(SUBJECT1, CONTENT1, user);
        Post post2 = PostTestObjectFactory.createPost(SUBJECT2, CONTENT2);
        Post post3 = PostTestObjectFactory.createPost(SUBJECT3, CONTENT3, user);

        postRepository.saveAll(List.of(post1, post2, post3));
        postRepository.delete(post1);

        // when
        boolean result1 = postRepository.existsByIdAndUserId(post1.getId(), user.getId());
        boolean result2 = postRepository.existsByIdAndUserId(post2.getId(), user.getId());
        boolean result3 = postRepository.existsByIdAndUserId(post3.getId(), user.getId());


        // then
        assertThat(result1).isFalse();
        assertThat(result2).isFalse();
        assertThat(result3).isTrue();
    }
}
