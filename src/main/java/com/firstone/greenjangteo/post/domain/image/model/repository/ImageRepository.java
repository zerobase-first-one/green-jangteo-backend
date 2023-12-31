package com.firstone.greenjangteo.post.domain.image.model.repository;

import com.firstone.greenjangteo.post.domain.image.model.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
