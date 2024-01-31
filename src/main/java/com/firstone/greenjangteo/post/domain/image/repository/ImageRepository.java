package com.firstone.greenjangteo.post.domain.image.repository;

import com.firstone.greenjangteo.post.domain.image.model.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findAllByPostIdOrderByIdAsc(Long postId);

    List<Image> findAllByCommentIdOrderByIdAsc(Long commentId);

    @Modifying
    @Query("DELETE FROM image i WHERE i.post.id = :postId")
    void deleteByPostId(@Param("postId") Long postId);

    @Modifying
    @Query("DELETE FROM image i WHERE i.comment.id = :commentId")
    void deleteByCommentId(@Param("commentId") Long commentId);

    @Modifying
    @Query("DELETE FROM image i WHERE i IN :images")
    void deleteAllInList(@Param("images") List<Image> images);
}
