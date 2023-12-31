package com.firstone.greenjangteo.post.service;

import com.firstone.greenjangteo.post.dto.PostRequestDto;
import com.firstone.greenjangteo.post.model.entity.Post;

public interface PostService {
    Post createPost(PostRequestDto postRegisterDto);
}
