package com.firstone.greenjangteo.post.service;

import com.firstone.greenjangteo.post.domain.image.dto.ImageRequestDto;
import com.firstone.greenjangteo.post.domain.image.model.entity.Image;
import com.firstone.greenjangteo.post.domain.image.model.repository.ImageRepository;
import com.firstone.greenjangteo.post.domain.image.testutil.ImageTestObjectFactory;
import com.firstone.greenjangteo.post.dto.PostRequestDto;
import com.firstone.greenjangteo.post.model.entity.Post;
import com.firstone.greenjangteo.post.repository.PostRepository;
import com.firstone.greenjangteo.post.utility.PostTestObjectFactory;
import com.firstone.greenjangteo.user.model.entity.User;
import com.firstone.greenjangteo.user.repository.UserRepository;
import com.firstone.greenjangteo.user.testutil.UserTestObjectFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;

import static com.firstone.greenjangteo.post.domain.image.testutil.ImageTestConstant.*;
import static com.firstone.greenjangteo.post.exception.message.NotFoundExceptionMessage.POSTED_USER_ID_NOT_FOUND_EXCEPTION;
import static com.firstone.greenjangteo.post.exception.message.NotFoundExceptionMessage.POST_NOT_FOUND_EXCEPTION;
import static com.firstone.greenjangteo.post.utility.PostTestConstant.*;
import static com.firstone.greenjangteo.user.model.Role.ROLE_BUYER;
import static com.firstone.greenjangteo.user.testutil.UserTestConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class PostServiceTest {
    @Autowired
    private PostService postService;

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

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @AfterEach
    void clearRedisCache() {
        Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection().flushDb();
    }

    @DisplayName("사용자 ID와 게시글 제목, 내용을 전송해 이미지가 없는 게시물을 등록할 수 있다.")
    @ParameterizedTest
    @CsvSource({
            "안녕하세요?, 12345",
            "abcde, ㄱㄴㄷ",
            "문의 드립니다, 가나다라 12345 aBc 가나다 ab 123"
    })
    void createPost(String subject, String content) {
        // given
        User user = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.name())
        );
        userRepository.save(user);

        PostRequestDto postRequestDto = PostTestObjectFactory
                .createPostRequestDto(user.getId().toString(), subject, content);

        // when
        Post savedPost = postService.createPost(postRequestDto);

        // then
        Post foundPost = postRepository.findById(savedPost.getId()).get();

        assertThat(savedPost.getUser().getId()).isEqualTo(user.getId());
        assertThat(savedPost.getSubject()).isEqualTo(subject);
        assertThat(savedPost.getContent()).isEqualTo(content);
        assertThat(savedPost.getCreatedAt()).isEqualTo(foundPost.getCreatedAt());
        assertThat(savedPost.getModifiedAt()).isEqualTo(foundPost.getModifiedAt());
    }

    @DisplayName("사용자 ID와 게시글 제목, 내용, 이미지들을 전송해 게시글과 포함된 이미지들을 등록할 수 있다.")
    @ParameterizedTest
    @CsvSource({
            "안녕하세요?, 12345",
            "abcde, ㄱㄴㄷ",
            "문의 드립니다, 가나다라 12345 aBc 가나다 ab 123"
    })
    void createPostWithImages(String subject, String content) {
        // given
        User user = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.name())
        );
        userRepository.save(user);

        List<ImageRequestDto> imageRequestDtos = ImageTestObjectFactory.createImageRequestDtos();
        PostRequestDto postRequestDto = PostTestObjectFactory
                .createPostRequestDto(user.getId().toString(), subject, content, imageRequestDtos);

        // when
        Post savedPost = postService.createPost(postRequestDto);

        // then
        Post foundPost = postRepository.findById(savedPost.getId()).get();

        assertThat(savedPost.getUser().getId()).isEqualTo(user.getId());
        assertThat(savedPost.getSubject()).isEqualTo(subject);
        assertThat(savedPost.getContent()).isEqualTo(content);
        assertThat(savedPost.getImages().size()).isEqualTo(imageRequestDtos.size());
        assertThat(savedPost.getCreatedAt()).isEqualTo(foundPost.getCreatedAt());
        assertThat(savedPost.getModifiedAt()).isEqualTo(foundPost.getModifiedAt());

        assertThat(savedPost.getImages()).hasSize(3)
                .extracting("url", "positionInContent")
                .containsExactlyInAnyOrder(
                        tuple(IMAGE_URL1, POSITION_IN_CONTENT),
                        tuple(IMAGE_URL2, POSITION_IN_CONTENT + 1),
                        tuple(IMAGE_URL3, POSITION_IN_CONTENT + 2)
                );
    }

    @DisplayName("게시물 ID와 게시자 ID를 전송해 게시글과 이미지들을 조회할 수 있다.")
    @ParameterizedTest
    @CsvSource({
            "안녕하세요?, 12345",
            "abcde, ㄱㄴㄷ",
            "문의 드립니다, 가나다라 12345 aBc 가나다 ab 123"
    })
    void getPost(String subject, String content) {
        User user = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.name())
        );
        userRepository.save(user);

        Post createdPost = PostTestObjectFactory.createPost(subject, content, user);
        postRepository.save(createdPost);

        List<Image> images = ImageTestObjectFactory.createImages(createdPost);
        imageRepository.saveAll(images);

        entityManager.refresh(createdPost);

        // when
        Post foundPost = postService.getPost(createdPost.getId(), user.getId());

        // then
        assertThat(foundPost.getId()).isEqualTo(createdPost.getId());
        assertThat(foundPost.getUser().getId()).isEqualTo(createdPost.getUser().getId());
        assertThat(foundPost.getSubject()).isEqualTo(createdPost.getSubject());
        assertThat(foundPost.getContent()).isEqualTo(createdPost.getContent());
        assertThat(foundPost.getCreatedAt()).isEqualTo(createdPost.getCreatedAt());
        assertThat(foundPost.getModifiedAt()).isEqualTo(createdPost.getModifiedAt());

        assertThat(foundPost.getImages()).hasSize(3)
                .extracting("url", "positionInContent")
                .containsExactlyInAnyOrder(
                        tuple(IMAGE_URL1, POSITION_IN_CONTENT),
                        tuple(IMAGE_URL2, POSITION_IN_CONTENT + 1),
                        tuple(IMAGE_URL3, POSITION_IN_CONTENT + 2)
                );
    }

    @DisplayName("잘못된 게시글 ID 또는 게시자 ID를 입력하면 EntityNotFoundException이 발생한다.")
    @Test
    void getPostFromWrongId() {
        // given
        User user = UserTestObjectFactory.createUser(
                EMAIL1, USERNAME1, PASSWORD1, passwordEncoder, FULL_NAME1, PHONE1, List.of(ROLE_BUYER.name())
        );
        userRepository.save(user);

        Post post = PostTestObjectFactory.createPost(SUBJECT1, CONTENT1, user);
        postRepository.save(post);

        // when, then
        assertThatThrownBy(() -> postService.getPost(post.getId() + 1, user.getId()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(POST_NOT_FOUND_EXCEPTION + (post.getId() + 1)
                        + POSTED_USER_ID_NOT_FOUND_EXCEPTION + user.getId());

        assertThatThrownBy(() -> postService.getPost(post.getId(), user.getId() + 1))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(POST_NOT_FOUND_EXCEPTION + post.getId()
                        + POSTED_USER_ID_NOT_FOUND_EXCEPTION + (user.getId() + 1));
    }

    @DisplayName("페이징 처리한 모든 게시글 목록을 게시글 생성 순서 내림차순으로 조회할 수 있다.")
    @Test
    void getPosts() {
        // given
        Post post1 = PostTestObjectFactory.createPost(SUBJECT1, CONTENT1);
        Post post2 = PostTestObjectFactory.createPost(SUBJECT2, CONTENT2);
        Post post3 = PostTestObjectFactory.createPost(SUBJECT3, CONTENT3);

        postRepository.saveAll(List.of(post1, post2, post3));

        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(0, 2, sort);

        // when
        List<Post> posts = postService.getPosts(pageable).getContent();

        // then
        assertThat(posts).hasSize(2)
                .extracting("subject", "content")
                .containsExactlyInAnyOrder(tuple(SUBJECT2, CONTENT2), tuple(SUBJECT3, CONTENT3));
    }
}