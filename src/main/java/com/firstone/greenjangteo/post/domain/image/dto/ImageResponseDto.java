package com.firstone.greenjangteo.post.domain.image.dto;

import com.firstone.greenjangteo.post.domain.image.model.entity.Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageResponseDto {
    private Long id;
    private String url;
    private int positionInContent;

    public static ImageResponseDto from(Image image) {
        return ImageResponseDto.builder()
                .id(image.getId())
                .url(image.getUrl())
                .positionInContent(image.getPositionInContent())
                .build();
    }
}
