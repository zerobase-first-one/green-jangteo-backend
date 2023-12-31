package com.firstone.greenjangteo.post.repository;

import com.firstone.greenjangteo.post.model.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAll(Pageable pageable);

    Optional<Post> findByIdAndUserId(Long id, Long userId);
}
