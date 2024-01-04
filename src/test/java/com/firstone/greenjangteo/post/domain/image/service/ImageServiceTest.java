package com.firstone.greenjangteo.post.domain.image.service;

import com.firstone.greenjangteo.post.domain.image.dto.ImageRequestDto;
import com.firstone.greenjangteo.post.domain.image.model.entity.Image;
import com.firstone.greenjangteo.post.domain.image.repository.ImageRepository;
import com.firstone.greenjangteo.post.domain.image.testutil.ImageTestObjectFactory;
import com.firstone.greenjangteo.post.model.entity.Post;
import com.firstone.greenjangteo.post.repository.PostRepository;
import com.firstone.greenjangteo.post.utility.PostTestObjectFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.firstone.greenjangteo.post.domain.image.testutil.ImageTestConstant.*;
import static com.firstone.greenjangteo.post.utility.PostTestConstant.CONTENT1;
import static com.firstone.greenjangteo.post.utility.PostTestConstant.SUBJECT1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class ImageServiceTest {
    @Autowired
    private ImageService imageService;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private PostRepository postRepository;

    @DisplayName("전송된 게시글 이미지의 목록을 저장할 수 있다.")
    @Test
    void saveImages() {
        // given
        Post post = PostTestObjectFactory.createPost(SUBJECT1, CONTENT1);
        postRepository.save(post);

        List<ImageRequestDto> imageRequestDtos = ImageTestObjectFactory.createImageRequestDtos();

        // when
        imageService.saveImages(post, imageRequestDtos);

        List<Image> images = imageRepository.findAll();

        // then
        assertThat(images).hasSize(3)
                .extracting("url", "positionInContent", "post")
                .containsExactlyInAnyOrder(
                        tuple(IMAGE_URL1, POSITION_IN_CONTENT, post),
                        tuple(IMAGE_URL2, POSITION_IN_CONTENT + 1, post),
                        tuple(IMAGE_URL3, POSITION_IN_CONTENT + 2, post)
                );
    }
}
