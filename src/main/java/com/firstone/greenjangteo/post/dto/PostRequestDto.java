package com.firstone.greenjangteo.post.dto;

import com.firstone.greenjangteo.post.domain.image.dto.ImageRequestDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

import static com.firstone.greenjangteo.web.ApiConstant.ID_EXAMPLE;
import static com.firstone.greenjangteo.web.ApiConstant.USER_ID_VALUE;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class PostRequestDto {
    private static final String SUBJECT_VALUE = "게시글 제목";
    private static final String SUBJECT_EXAMPLE = "문의 드립니다.";
    private static final String SUBJECT_NO_VALUE_EXCEPTION_MESSAGE = "게시글 제목을 입력하세요.";

    private static final String CONTENT_VALUE = "게시글 내용";
    private static final String CONTENT_EXAMPLE = "문의 내용";
    private static final String CONTENT_NO_VALUE_EXCEPTION_MESSAGE = "게시글 내용을 입력하세요.";

    private static final String IMAGE_REQUEST_DTOS_VALUE = "게시글에 포함되는 이미지들";

    @ApiModelProperty(value = USER_ID_VALUE, example = ID_EXAMPLE)
    private String userId;

    @ApiModelProperty(value = SUBJECT_VALUE, example = SUBJECT_EXAMPLE)
    @NotBlank(message = SUBJECT_NO_VALUE_EXCEPTION_MESSAGE)
    private String subject;

    @ApiModelProperty(value = CONTENT_VALUE, example = CONTENT_EXAMPLE)
    @NotBlank(message = CONTENT_NO_VALUE_EXCEPTION_MESSAGE)
    private String content;

    @ApiModelProperty(value = IMAGE_REQUEST_DTOS_VALUE)
    private List<ImageRequestDto> imageRequestDtos;
}
