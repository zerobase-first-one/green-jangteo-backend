package com.firstone.greenjangteo.post.domain.image.testutil;

import com.firstone.greenjangteo.post.domain.image.dto.ImageRequestDto;
import com.firstone.greenjangteo.post.domain.image.model.entity.Image;
import com.firstone.greenjangteo.post.model.entity.Post;

import java.util.List;

import static com.firstone.greenjangteo.post.domain.image.testutil.ImageTestConstant.*;

public class ImageTestObjectFactory {
    public static List<ImageRequestDto> createImageRequestDtos() {
        int position = POSITION_IN_CONTENT;

        ImageRequestDto imageRequestDto1 = createImageRequestDto(IMAGE_URL1, position++);
        ImageRequestDto imageRequestDto2 = createImageRequestDto(IMAGE_URL2, position++);
        ImageRequestDto imageRequestDto3 = createImageRequestDto(IMAGE_URL3, position);

        return List.of(imageRequestDto1, imageRequestDto2, imageRequestDto3);
    }

    public static List<ImageRequestDto> createImageUpdateRequestDtos() {
        int position = POSITION_IN_CONTENT;

        ImageRequestDto imageRequestDto2 = createImageRequestDto(IMAGE_URL3, position++);
        ImageRequestDto imageRequestDto3 = createImageRequestDto(IMAGE_URL2, ++position);
        ImageRequestDto imageRequestDto1 = createImageRequestDto(IMAGE_URL1, ++position);

        return List.of(imageRequestDto1, imageRequestDto2, imageRequestDto3);
    }

    public static List<Image> createImages(Post post) {
        int position = POSITION_IN_CONTENT;

        Image image1 = createImage(IMAGE_URL1, position++, post);
        Image image2 = createImage(IMAGE_URL2, position++, post);
        Image image3 = createImage(IMAGE_URL3, position, post);

        return List.of(image1, image2, image3);
    }

    public static List<Image> createImages() {
        int position = POSITION_IN_CONTENT;

        Image image1 = createImage(IMAGE_URL1, position++);
        Image image2 = createImage(IMAGE_URL2, position++);
        Image image3 = createImage(IMAGE_URL3, position);

        return List.of(image1, image2, image3);
    }

    private static ImageRequestDto createImageRequestDto(String imageUrl, int position) {
        return ImageRequestDto.builder()
                .url(imageUrl)
                .positionInContent(position)
                .build();
    }

    private static Image createImage(String imageUrl, int position, Post post) {
        return Image.builder()
                .url(imageUrl)
                .positionInContent(position)
                .post(post)
                .build();
    }

    private static Image createImage(String imageUrl, int position) {
        return Image.builder()
                .url(imageUrl)
                .positionInContent(position)
                .build();
    }
}
