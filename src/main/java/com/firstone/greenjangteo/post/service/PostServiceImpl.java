package com.firstone.greenjangteo.post.service;

import com.firstone.greenjangteo.post.domain.image.service.ImageService;
import com.firstone.greenjangteo.post.dto.PostRequestDto;
import com.firstone.greenjangteo.post.model.entity.Post;
import com.firstone.greenjangteo.post.repository.PostRepository;
import com.firstone.greenjangteo.user.model.entity.User;
import com.firstone.greenjangteo.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;

import static com.firstone.greenjangteo.post.exception.message.NotFoundExceptionMessage.POSTED_USER_ID_NOT_FOUND_EXCEPTION;
import static com.firstone.greenjangteo.post.exception.message.NotFoundExceptionMessage.POST_NOT_FOUND_EXCEPTION;
import static org.springframework.transaction.annotation.Isolation.*;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final UserService userService;
    private final ImageService imageService;
    private final PostRepository postRepository;

    private final EntityManager entityManager;

    private static final String CREATE_POST_KEY = "#result.id + '_' + #postRequestDto.userId";
    private static final String CREATE_KEY_CONDITION = "#postRequestDto != null && #postRequestDto.userId != null";

    private static final String GET_POST_KEY = "#postId + '_' + #writerId";
    private static final String GET_KEY_CONDITION = "#postId != null && #writerId != null";

    private static final String KEY_VALUE = "post";
    private static final String UNLESS_CONDITION = "#result == null";

    private static final String UPDATE_POST_KEY = "#postId + '_' + #postRequestDto.userId";
    private static final String UPDATE_KEY_CONDITION
            = "#postId != null && #postRequestDto != null && #postRequestDto.userId != null";

    private static final String DELETE_POST_KEY = "#postId + '_' + #userId";
    private static final String DELETE_KEY_CONDITION = "#postId != null && #userId != null";

    @Override
    @Transactional(isolation = READ_UNCOMMITTED, timeout = 20)
    @CachePut(key = CREATE_POST_KEY, condition = CREATE_KEY_CONDITION, unless = UNLESS_CONDITION, value = KEY_VALUE)
    public Post createPost(PostRequestDto postRequestDto) {
        User user = userService.getUser(Long.parseLong(postRequestDto.getUserId()));
        Post post = postRepository.save(Post.from(postRequestDto, user));

        if (postRequestDto.getImageRequestDtos() != null) {
            imageService.saveImages(post, postRequestDto.getImageRequestDtos());
            entityManager.refresh(post);
        }

        return post;
    }

    @Override
    @Transactional(isolation = READ_COMMITTED, readOnly = true, timeout = 10)
    public Page<Post> getPosts(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    @Override
    public Page<Post> getPosts(Pageable pageable, Long userId) {
        return postRepository.findByUserId(userId, pageable);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED, readOnly = true, timeout = 15)
    @Cacheable(key = GET_POST_KEY, condition = GET_KEY_CONDITION, unless = UNLESS_CONDITION, value = KEY_VALUE)
    public Post getPost(Long postId, Long writerId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException
                        (POST_NOT_FOUND_EXCEPTION + postId + POSTED_USER_ID_NOT_FOUND_EXCEPTION + writerId));

        return post;
    }

    @Override
    public Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(POST_NOT_FOUND_EXCEPTION + postId));
    }

    @Override
    @Transactional(isolation = REPEATABLE_READ, timeout = 30)
    @CachePut(key = UPDATE_POST_KEY, condition = UPDATE_KEY_CONDITION, unless = UNLESS_CONDITION, value = KEY_VALUE)
    public Post updatePost(Long postId, PostRequestDto postRequestDto) {
        Post post = getPost(postId, Long.parseLong(postRequestDto.getUserId()));
        Post updatedPost = postRepository.save(post.updateFrom(postRequestDto));

        imageService.updateImages(updatedPost, postRequestDto.getImageRequestDtos());

        entityManager.refresh(updatedPost);

        return updatedPost;
    }

    @Override
    @Transactional(isolation = READ_COMMITTED, timeout = 15)
    @CacheEvict(key = DELETE_POST_KEY, condition = DELETE_KEY_CONDITION, value = KEY_VALUE)
    public void deletePost(Long postId, Long userId) {
        if (postRepository.existsByIdAndUserId(postId, userId)) {
            postRepository.deleteById(postId);
            return;
        }

        throw new EntityNotFoundException(
                POST_NOT_FOUND_EXCEPTION + postId + POSTED_USER_ID_NOT_FOUND_EXCEPTION + userId
        );
    }
}
