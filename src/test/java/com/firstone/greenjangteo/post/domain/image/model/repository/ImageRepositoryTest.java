package com.firstone.greenjangteo.post.domain.image.model.repository;

import com.firstone.greenjangteo.post.domain.comment.model.entity.Comment;
import com.firstone.greenjangteo.post.domain.comment.repository.CommentRepository;
import com.firstone.greenjangteo.post.domain.comment.service.CommentTestObjectFactory;
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
class ImageRepositoryTest {
    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @DisplayName("전송된 게시글 ID를 가지는 게시글의 이미지 목록을 ID 오름차순으로 검색할 수 있다.")
    @Test
    void findAllByPostIdOrderByIdAsc() {

        // given
        Post post = PostTestObjectFactory.createPost(SUBJECT1, CONTENT1);
        postRepository.save(post);

        List<Image> images = ImageTestObjectFactory.createImages(post);
        imageRepository.saveAll(images);

        // when
        List<Image> foundImages = imageRepository.findAllByPostIdOrderByIdAsc(post.getId());

        // then
        assertThat(foundImages).hasSize(3)
                .extracting("url", "positionInContent")
                .containsExactly(
                        tuple(IMAGE_URL1, POSITION_IN_CONTENT),
                        tuple(IMAGE_URL2, POSITION_IN_CONTENT + 1),
                        tuple(IMAGE_URL3, POSITION_IN_CONTENT + 2)
                );
    }

    @DisplayName("전송된 댓글 ID를 가지는 댓글의 이미지 목록을 ID 오름차순으로 검색할 수 있다.")
    @Test
    void findAllByCommentIdOrderByIdAsc() {
        // given
        Comment comment = CommentTestObjectFactory.createComment(CONTENT1);
        commentRepository.save(comment);

        List<Image> images = ImageTestObjectFactory.createImages(comment);
        imageRepository.saveAll(images);

        // when
        List<Image> foundImages = imageRepository.findAllByCommentIdOrderByIdAsc(comment.getId());

        // then
        assertThat(foundImages).hasSize(3)
                .extracting("url", "positionInContent")
                .containsExactly(
                        tuple(IMAGE_URL1, POSITION_IN_CONTENT),
                        tuple(IMAGE_URL2, POSITION_IN_CONTENT + 1),
                        tuple(IMAGE_URL3, POSITION_IN_CONTENT + 2)
                );
    }

    @DisplayName("게시글 ID를 전송해 게시글의 이미지들을 삭제할 수 있다.")
    @Test
    void deleteByPostId() {
        // given
        Post post = PostTestObjectFactory.createPost(SUBJECT1, CONTENT1);
        postRepository.save(post);

        List<Image> createdImages = ImageTestObjectFactory.createImages(post);
        imageRepository.saveAll(createdImages);

        Long postId = post.getId();

        // when
        imageRepository.deleteByPostId(postId);
        List<Image> foundImages = imageRepository.findAllByPostIdOrderByIdAsc(postId);

        // then
        assertThat(foundImages).isEmpty();
    }

    @DisplayName("댓글 ID를 전송해 댓글의 이미지들을 삭제할 수 있다.")
    @Test
    void deleteByCommentId() {
        // given
        Comment comment = CommentTestObjectFactory.createComment(CONTENT1);
        commentRepository.save(comment);

        List<Image> images = ImageTestObjectFactory.createImages(comment);
        imageRepository.saveAll(images);

        Long commentId = comment.getId();

        // when
        imageRepository.deleteByCommentId(commentId);
        List<Image> foundImages = imageRepository.findAllByCommentIdOrderByIdAsc(commentId);

        // then
        assertThat(foundImages).isEmpty();
    }

    @DisplayName("이미지 목록을 전송해 게시글의 이미지들을 삭제할 수 있다.")
    @Test
    void deleteAllInList() {
        // given
        Post post = PostTestObjectFactory.createPost(SUBJECT1, CONTENT1);
        postRepository.save(post);

        List<Image> createdImages = ImageTestObjectFactory.createImages(post);
        imageRepository.saveAll(createdImages);

        // when
        imageRepository.deleteAllInList(createdImages);
        List<Image> foundImages = imageRepository.findAllByPostIdOrderByIdAsc(post.getId());

        // then
        assertThat(foundImages).isEmpty();
    }
}