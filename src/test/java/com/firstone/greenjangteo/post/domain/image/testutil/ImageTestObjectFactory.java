package com.firstone.greenjangteo.post.domain.image.testutil;

import com.firstone.greenjangteo.post.domain.image.dto.ImageRequestDto;
import com.firstone.greenjangteo.post.domain.image.model.entity.Image;

import java.util.List;

import static com.firstone.greenjangteo.post.domain.image.testutil.ImageTestConstant.*;

public class ImageTestObjectFactory {
    private static int positionInContent = 10;

    public static List<ImageRequestDto> createImageRequestDtos() {
        int position = POSITION_IN_CONTENT;

        ImageRequestDto imageRequestDto1 = createImageRequestDto(IMAGE_URL1, position++);
        ImageRequestDto imageRequestDto2 = createImageRequestDto(IMAGE_URL2, position++);
        ImageRequestDto imageRequestDto3 = createImageRequestDto(IMAGE_URL3, position);

        return List.of(imageRequestDto1, imageRequestDto2, imageRequestDto3);
    }

    public static List<Image> createImages() {
        Image image1 = createImage(IMAGE_URL1);
        Image image2 = createImage(IMAGE_URL2);
        Image image3 = createImage(IMAGE_URL3);

        return List.of(image1, image2, image3);
    }

    private static ImageRequestDto createImageRequestDto(String imageUrl, int position) {
        return ImageRequestDto.builder()
                .url(imageUrl)
                .positionInContent(position)
                .build();
    }

    private static Image createImage(String imageUrl) {
        return Image.builder()
                .url(imageUrl)
                .positionInContent(positionInContent++)
                .build();
    }
}
