package com.firstone.greenjangteo.product.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Builder
@Getter
public class CreateReviewForm {

    @ApiModelProperty(value = "회원ID", example = "1")
    private Long userId;

    @ApiModelProperty(value = "상품ID", example = "2")
    private Long productId;

    @ApiModelProperty(value = "내용", example = "에코백이 참 좋네요!")
    private String content;

    @ApiModelProperty(value = "별점", example = "5")
    private int score;

    @ApiModelProperty(value = "첨부 이미지", example = "")
    private String imageUrl;
}
