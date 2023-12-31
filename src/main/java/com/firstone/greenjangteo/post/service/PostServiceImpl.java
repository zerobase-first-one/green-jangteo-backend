package com.firstone.greenjangteo.post.service;

import com.firstone.greenjangteo.post.domain.image.service.ImageService;
import com.firstone.greenjangteo.post.dto.PostRequestDto;
import com.firstone.greenjangteo.post.model.entity.Post;
import com.firstone.greenjangteo.post.repository.PostRepository;
import com.firstone.greenjangteo.user.model.entity.User;
import com.firstone.greenjangteo.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;

import static com.firstone.greenjangteo.post.exception.message.NotFoundExceptionMessage.POSTED_USER_ID_NOT_FOUND_EXCEPTION;
import static com.firstone.greenjangteo.post.exception.message.NotFoundExceptionMessage.POST_NOT_FOUND_EXCEPTION;
import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Isolation.READ_UNCOMMITTED;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final UserService userService;
    private final ImageService imageService;
    private final PostRepository postRepository;

    private final EntityManager entityManager;

    private static final String RESULT_KEY = "#result.id";
    private static final String CREATE_KEY_CONDITION = "#postRequestDto != null &&#postRequestDto.userId != null";

    private static final String REQUEST_KEY = "#postId";
    private static final String GET_KEY_CONDITION = "#postId != null && #writerId != null";

    private static final String KEY_VALUE = "post";
    private static final String UNLESS_CONDITION = "#result == null";

    @Override
    @Transactional(isolation = READ_UNCOMMITTED, timeout = 20)
    @CachePut(key = RESULT_KEY, condition = CREATE_KEY_CONDITION, unless = UNLESS_CONDITION, value = KEY_VALUE)
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
    @Transactional(isolation = READ_COMMITTED, timeout = 15)
    @Cacheable(key = REQUEST_KEY, condition = GET_KEY_CONDITION, unless = UNLESS_CONDITION, value = KEY_VALUE)
    public Post getPost(Long postId, Long writerId) {
        Post post = postRepository.findByIdAndUserId(postId, writerId)
                .orElseThrow(() -> new EntityNotFoundException
                        (POST_NOT_FOUND_EXCEPTION + postId + POSTED_USER_ID_NOT_FOUND_EXCEPTION + writerId));

        return post;
    }
}
