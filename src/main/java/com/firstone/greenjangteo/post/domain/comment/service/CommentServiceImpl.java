package com.firstone.greenjangteo.post.domain.comment.service;

import com.firstone.greenjangteo.post.domain.comment.dto.CommentRequestDto;
import com.firstone.greenjangteo.post.domain.comment.exception.serious.InconsistentCommentException;
import com.firstone.greenjangteo.post.domain.comment.model.entity.Comment;
import com.firstone.greenjangteo.post.domain.comment.repository.CommentRepository;
import com.firstone.greenjangteo.post.domain.image.service.ImageService;
import com.firstone.greenjangteo.post.model.entity.Post;
import com.firstone.greenjangteo.post.service.PostService;
import com.firstone.greenjangteo.user.model.entity.User;
import com.firstone.greenjangteo.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;

import static com.firstone.greenjangteo.post.domain.comment.exception.message.InconsistentExceptionMessage.INCONSISTENT_COMMENT_EXCEPTION_COMMENT_ID;
import static com.firstone.greenjangteo.post.domain.comment.exception.message.InconsistentExceptionMessage.INCONSISTENT_COMMENT_EXCEPTION_POST_ID;
import static com.firstone.greenjangteo.post.domain.comment.exception.message.NotFoundExceptionMessage.COMMENTED_USER_ID_NOT_FOUND_EXCEPTION;
import static com.firstone.greenjangteo.post.domain.comment.exception.message.NotFoundExceptionMessage.COMMENT_NOT_FOUND_EXCEPTION;
import static com.firstone.greenjangteo.post.exception.message.NotFoundExceptionMessage.POSTED_USER_ID_NOT_FOUND_EXCEPTION;
import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Isolation.REPEATABLE_READ;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final PostService postService;
    private final ImageService imageService;

    private final EntityManager entityManager;

    @Override
    @Transactional(isolation = READ_COMMITTED, timeout = 20)
    public Comment createComment(CommentRequestDto commentRequestDto) {
        User user = userService.getUser(Long.parseLong(commentRequestDto.getUserId()));
        Post post = postService.getPost(Long.parseLong(commentRequestDto.getPostId()));
        Comment comment = commentRepository.save(Comment.of(commentRequestDto.getContent(), user, post));

        if (commentRequestDto.getImageRequestDtos() != null) {
            imageService.saveImages(comment, commentRequestDto.getImageRequestDtos());
            entityManager.refresh(comment);
        }

        return comment;
    }

    @Override
    @Transactional(isolation = REPEATABLE_READ, readOnly = true, timeout = 15)
    public Page<Comment> getComments(Pageable pageable, Long postId) {
        return commentRepository.findByPostId(postId, pageable);
    }

    @Override
    public Comment getComment(Long commentId, Long writerId) {
        return commentRepository.findByIdAndUserId(commentId, writerId)
                .orElseThrow(() -> new EntityNotFoundException
                        (COMMENT_NOT_FOUND_EXCEPTION + commentId + COMMENTED_USER_ID_NOT_FOUND_EXCEPTION + writerId));
    }

    @Override
    @Transactional(isolation = READ_COMMITTED, timeout = 15)
    public Comment updateComment(Long commentId, CommentRequestDto commentRequestDto) {
        Comment comment = getComment(commentId, Long.parseLong(commentRequestDto.getUserId()));
        validatePostId(comment, Long.parseLong(commentRequestDto.getPostId()));

        Comment updatedComment = commentRepository.save(comment.updateFrom(commentRequestDto));

        imageService.updateImages(updatedComment, commentRequestDto.getImageRequestDtos());

        entityManager.flush();
        entityManager.refresh(updatedComment);

        return updatedComment;
    }

    @Override
    @Transactional(isolation = READ_COMMITTED, timeout = 10)
    public void deleteComment(Long commentId, Long userId) {
        if (commentRepository.existsByIdAndUserId(commentId, userId)) {
            commentRepository.deleteById(commentId);
            return;
        }

        throw new EntityNotFoundException(
                COMMENT_NOT_FOUND_EXCEPTION + commentId + COMMENTED_USER_ID_NOT_FOUND_EXCEPTION + userId
        );
    }

    @Override
    @Transactional(isolation = READ_COMMITTED, readOnly = true, timeout = 5)
    public int getCommentCountForPost(Long postId) {
        return commentRepository.countByPostId(postId).intValue();
    }

    private void validatePostId(Comment comment, Long requestedPostId) {
        if (comment.getPost().getId().equals(requestedPostId)) {
            return;
        }

        throw new InconsistentCommentException(INCONSISTENT_COMMENT_EXCEPTION_POST_ID + requestedPostId
                + INCONSISTENT_COMMENT_EXCEPTION_COMMENT_ID + comment.getId()
        );
    }
}
