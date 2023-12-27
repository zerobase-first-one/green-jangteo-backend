package com.firstone.greenjangteo.coupon.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

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
    private static final String SCHEDULED_ISSUE_DATE_EXAMPLE = "2023-12-25";
    private static final String SCHEDULED_ISSUE_DATE_NO_VALUE = "발행 예정일은 필수값입니다.";
    private static final String NOT_FUTURE_SCHEDULED_ISSUE_DATE = "발행 예정일은 내일 이후의 날짜를 선택해야 합니다.";

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
    @NotNull(message = SCHEDULED_ISSUE_DATE_NO_VALUE)
    @Future(message = NOT_FUTURE_SCHEDULED_ISSUE_DATE)
    private LocalDate scheduledIssueDate;

    @ApiModelProperty(value = EXPIRATION_PERIOD_VALUE, example = EXPIRATION_PERIOD_EXAMPLE)
    private String expirationPeriod;

    public boolean isIssueQuantityIsMinusOne() {
        return issueQuantity.equals("-1");
    }

    public void setIssueQuantityToZero() {
        issueQuantity = "0";
    }
}
