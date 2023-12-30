package com.firstone.greenjangteo.post.service;

import com.firstone.greenjangteo.post.domain.image.dto.ImageRequestDto;
import com.firstone.greenjangteo.post.domain.image.testutil.ImageTestObjectFactory;
import com.firstone.greenjangteo.post.dto.PostRequestDto;
import com.firstone.greenjangteo.post.model.entity.Post;
import com.firstone.greenjangteo.post.repository.PostRepository;
import com.firstone.greenjangteo.post.utility.PostTestObjectFactory;
import com.firstone.greenjangteo.user.model.entity.User;
import com.firstone.greenjangteo.user.repository.UserRepository;
import com.firstone.greenjangteo.user.testutil.UserTestObjectFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.firstone.greenjangteo.post.domain.image.testutil.ImageTestConstant.*;
import static com.firstone.greenjangteo.user.model.Role.ROLE_BUYER;
import static com.firstone.greenjangteo.user.testutil.UserTestConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
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
    private PasswordEncoder passwordEncoder;

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
    // Post에서 Image를 로딩하는 fetch type이 LAZY이므로, 영속성 컨텍스트를 유지해서 LazyInitializationException 방지
    @Transactional
    void registerPostWithImages(String subject, String content) {
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
}