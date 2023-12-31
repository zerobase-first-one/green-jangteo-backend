package com.firstone.greenjangteo.post.repository;

import com.firstone.greenjangteo.post.model.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
