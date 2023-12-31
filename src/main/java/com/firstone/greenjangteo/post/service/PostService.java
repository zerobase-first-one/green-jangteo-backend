package com.firstone.greenjangteo.post.service;

import com.firstone.greenjangteo.post.dto.PostRequestDto;
import com.firstone.greenjangteo.post.model.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {
    Post createPost(PostRequestDto postRegisterDto);

    Page<Post> getPosts(Pageable pageable);

    Page<Post> getPosts(Pageable pageable, Long userId);

    Post getPost(Long postId, Long writerId);
}
