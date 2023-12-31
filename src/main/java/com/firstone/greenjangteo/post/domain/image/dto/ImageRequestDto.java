package com.firstone.greenjangteo.post.domain.image.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageRequestDto {
    private static final String URL_VALUE = "이미지 URL";
    private static final String URL_EXAMPLE
            = "https://test-images-bucket.s3.us-west-1.amazonaws.com/images/sample1.jpg";

    private static final String POSITION_IN_CONTENT_VALUE = "게시물, 댓글 내용에서 이미지의 위치";
    private static final String POSITION_IN_CONTENT_EXAMPLE = "10";

    @ApiModelProperty(value = URL_VALUE, example = URL_EXAMPLE)
    private String url;

    @ApiModelProperty(value = POSITION_IN_CONTENT_VALUE, example = POSITION_IN_CONTENT_EXAMPLE)
    private int positionInContent;
}
