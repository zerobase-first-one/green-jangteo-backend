package com.firstone.greenjangteo.coupon.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class IssueCouponsRequestDto {
    private static final String COUPON_NAME_VALUE = "쿠폰 이름";
    private static final String COUPON_NAME_EXAMPLE = "연말 할인 이벤트 쿠폰";

    private static final String AMOUNT_VALUE = "쿠폰 금액";
    private static final String AMOUNT_EXAMPLE = "5000";

    private static final String DESCRIPTION_VALUE = "쿠폰 설명";
    private static final String DESCRIPTION_EXAMPLE = "모든 구매자 고객을 대상으로 지급되는 이벤트 쿠폰";

    private static final String ISSUE_QUANTITY_VALUE = "발행 매수";
    private static final String ISSUE_QUANTITY_EXAMPLE = "100";

    private static final String SCHEDULED_ISSUE_DATE_VALUE = "쿠폰 발행 예정일";
    private static final String SCHEDULED_ISSUE_DATE_EXAMPLE = "2024-12-25T00:00:00";
    private static final String SCHEDULED_ISSUE_DATE_NO_VALUE = "발행 예정일은 필수값입니다.";
    private static final String NOT_FUTURE_SCHEDULED_ISSUE_DATE = "발행 예정일은 현재 이후의 시간을 선택해야 합니다.";

    private static final String EXPIRATION_PERIOD_VALUE = "쿠폰 유효기간(일)";
    private static final String EXPIRATION_PERIOD_EXAMPLE = "30";

    @ApiModelProperty(value = COUPON_NAME_VALUE, example = COUPON_NAME_EXAMPLE)
    private String couponName;

    @ApiModelProperty(value = AMOUNT_VALUE, example = AMOUNT_EXAMPLE)
    private String amount;

    @ApiModelProperty(value = DESCRIPTION_VALUE, example = DESCRIPTION_EXAMPLE)
    private String description;

    @ApiModelProperty(value = ISSUE_QUANTITY_VALUE, example = ISSUE_QUANTITY_EXAMPLE)
    private String issueQuantity;

    @ApiModelProperty(value = SCHEDULED_ISSUE_DATE_VALUE, example = SCHEDULED_ISSUE_DATE_EXAMPLE)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @NotNull(message = SCHEDULED_ISSUE_DATE_NO_VALUE)
    @FutureOrPresent(message = NOT_FUTURE_SCHEDULED_ISSUE_DATE)
    private LocalDateTime scheduledIssueDate;

    @ApiModelProperty(value = EXPIRATION_PERIOD_VALUE, example = EXPIRATION_PERIOD_EXAMPLE)
    private String expirationPeriod;
}
