package com.firstone.greenjangteo.post.domain.image.service;

import com.firstone.greenjangteo.post.domain.comment.model.entity.Comment;
import com.firstone.greenjangteo.post.domain.comment.repository.CommentRepository;
import com.firstone.greenjangteo.post.domain.comment.service.CommentTestObjectFactory;
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

    @Autowired
    private CommentRepository commentRepository;

    @DisplayName("전송된 게시글 이미지의 목록을 저장할 수 있다.")
    @Test
    void savePostImages() {
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

    @DisplayName("전송된 게시글 이미지의 목록을 수정할 수 있다.")
    @Test
    void updatePostImages() {
        // given
        Post post = PostTestObjectFactory.createPost(SUBJECT1, CONTENT1);
        postRepository.save(post);

        List<Image> createdImages = ImageTestObjectFactory.createImages(post);
        imageRepository.saveAll(createdImages);

        List<ImageRequestDto> imageRequestDtos = ImageTestObjectFactory.createImageUpdateRequestDtos();

        // when
        imageService.updateImages(post, imageRequestDtos);
        List<Image> updatedImages = imageRepository.findAll();

        // then
        assertThat(updatedImages).hasSize(3)
                .extracting("url", "positionInContent", "post")
                .containsExactlyInAnyOrder(
                        tuple(IMAGE_URL3, POSITION_IN_CONTENT, post),
                        tuple(IMAGE_URL2, POSITION_IN_CONTENT + 2, post),
                        tuple(IMAGE_URL1, POSITION_IN_CONTENT + 3, post)
                );
    }

    @DisplayName("전송된 댓글 이미지의 목록을 저장할 수 있다.")
    @Test
    void saveCommentImages() {
        // given
        Comment comment = CommentTestObjectFactory.createComment(CONTENT1);
        commentRepository.save(comment);

        List<ImageRequestDto> imageRequestDtos = ImageTestObjectFactory.createImageRequestDtos();

        // when
        imageService.saveImages(comment, imageRequestDtos);

        List<Image> images = imageRepository.findAll();

        // then
        assertThat(images).hasSize(3)
                .extracting("url", "positionInContent", "comment")
                .containsExactlyInAnyOrder(
                        tuple(IMAGE_URL1, POSITION_IN_CONTENT, comment),
                        tuple(IMAGE_URL2, POSITION_IN_CONTENT + 1, comment),
                        tuple(IMAGE_URL3, POSITION_IN_CONTENT + 2, comment)
                );
    }

    @DisplayName("전송된 댓글 이미지의 목록을 수정할 수 있다.")
    @Test
    void updateCommentImages() {
        // given
        Comment comment = CommentTestObjectFactory.createComment(CONTENT1);
        commentRepository.save(comment);

        List<Image> createdImages = ImageTestObjectFactory.createImages(comment);
        imageRepository.saveAll(createdImages);

        List<ImageRequestDto> imageRequestDtos = ImageTestObjectFactory.createImageUpdateRequestDtos();

        // when
        imageService.updateImages(comment, imageRequestDtos);
        List<Image> updatedImages = imageRepository.findAll();

        // then
        assertThat(updatedImages).hasSize(3)
                .extracting("url", "positionInContent", "comment")
                .containsExactlyInAnyOrder(
                        tuple(IMAGE_URL3, POSITION_IN_CONTENT, comment),
                        tuple(IMAGE_URL2, POSITION_IN_CONTENT + 2, comment),
                        tuple(IMAGE_URL1, POSITION_IN_CONTENT + 3, comment)
                );
    }
}
