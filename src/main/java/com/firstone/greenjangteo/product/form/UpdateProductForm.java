package com.firstone.greenjangteo.product.form;

import com.firstone.greenjangteo.product.domain.dto.CategoryDetailDto;
import com.firstone.greenjangteo.product.domain.dto.CategoryDto;
import com.firstone.greenjangteo.product.domain.dto.ProductImageDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Builder
@Getter
public class UpdateProductForm {
    @ApiModelProperty(value = "productId", example = "2")
    private Long productId;

    @ApiModelProperty(value = "상품명", example = "New 업사이클링 에코백")
    private String productName;

    @ApiModelProperty(value = "카테고리", example = "[{\"category\": \"생활용품\"}, {\"category\": \"New에코백\"}]")
    private List<CategoryDetailDto> categories;

    @ApiModelProperty(value = "재고", example = "20")
    private int inventory;

    @ApiModelProperty(value = "가격", example = "20000")
    private int price;

    @ApiModelProperty(value = "상세설명", example = "이 제품은 폐의류를 가공하여 만든 에코백입니다.")
    private String description;

    @ApiModelProperty(value = "이미지 저장 경로", example = "C:/greenjangteo/product")
    private String imageStoragePath;

    @ApiModelProperty(value = "상품사진", example = "[{\"url\" : \"3.jpg\", \"position\" : \"0\"}, {\"url\" : \"4.jpg\", \"position\" : \"1\"}]")
    private List<ProductImageDto> images;
}
