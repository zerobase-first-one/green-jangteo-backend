package com.firstone.greenjangteo.product.domain.dto;

import com.firstone.greenjangteo.product.domain.model.ProductImage;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageDto {
    private String url;
    private int position;
    public static ImageDto toImageDto(ProductImage productImage) {
        return ImageDto.builder()
                .url(productImage.getUrl())
                .position(productImage.getPosition())
                .build();
    }
}
