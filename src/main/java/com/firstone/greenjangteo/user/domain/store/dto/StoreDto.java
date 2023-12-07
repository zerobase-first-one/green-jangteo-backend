package com.firstone.greenjangteo.user.domain.store.dto;

import com.firstone.greenjangteo.user.domain.store.service.model.entity.Store;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class StoreDto {
    private static final String STORE_NAME_VALUE = "가게 이름";
    private static final String STORE_NAME_EXAMPLE = "친환경 스토어";

    private static final String DESCRIPTION_VALUE = "가게 설명";
    private static final String DESCRIPTION_EXAMPLE = "좋은 가게입니다.";

    private static final String IMAGE_URL_VALUE = "이미지 URL";
    private static final String IMAGE_URL_EXAMPLE
            = "https://test-images-bucket.s3.us-west-1.amazonaws.com/images/sample1.jpg";


    @ApiModelProperty(value = STORE_NAME_VALUE, example = STORE_NAME_EXAMPLE)
    private String storeName;

    @ApiModelProperty(value = DESCRIPTION_VALUE, example = DESCRIPTION_EXAMPLE)
    private String description;

    @ApiModelProperty(value = IMAGE_URL_VALUE, example = IMAGE_URL_EXAMPLE)
    private String imageUrl;

    public static StoreDto of(String storeName, String description, String imageUrl) {
        return new StoreDto(storeName, description, imageUrl);
    }

    public static StoreDto from(Store store) {
        return new StoreDto(store.getStoreName().getValue(), store.getDescription(), store.getImageUrl());
    }
}
