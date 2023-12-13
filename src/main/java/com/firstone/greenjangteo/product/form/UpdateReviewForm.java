package com.firstone.greenjangteo.product.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Builder
@Getter
public class UpdateReviewForm {
    @ApiModelProperty(value = "리뷰ID", example = "1")
    private Long reviewId;

    @ApiModelProperty(value = "내용", example = "이 에코백은 외관도 멋드러지고, 내구성도 좋습니다!")
    private String content;

    @ApiModelProperty(value = "별점", example = "5")
    private int score;

    @ApiModelProperty(value = "첨부 이미지", example = "")
    private String imageUrl;
}
