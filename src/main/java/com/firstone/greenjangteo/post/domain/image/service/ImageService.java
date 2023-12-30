package com.firstone.greenjangteo.post.domain.image.service;

import com.firstone.greenjangteo.post.domain.image.dto.ImageRequestDto;
import com.firstone.greenjangteo.post.domain.image.model.entity.Image;
import com.firstone.greenjangteo.post.domain.image.model.repository.ImageRepository;
import com.firstone.greenjangteo.post.model.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;

    public void saveImages(Post post, List<ImageRequestDto> imageRequestDtos) {
        List<Image> images = imageRequestDtos.stream()
                .map(imageRequestDto -> Image.from(post, imageRequestDto))
                .collect(Collectors.toList());

        imageRepository.saveAll(images);
    }
}
