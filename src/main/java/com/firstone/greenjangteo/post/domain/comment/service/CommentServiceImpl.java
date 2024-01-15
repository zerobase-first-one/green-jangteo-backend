package com.firstone.greenjangteo.post.domain.comment.service;

import com.firstone.greenjangteo.post.domain.comment.dto.CommentRequestDto;
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
}
