package com.firstone.greenjangteo.coupon.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static com.firstone.greenjangteo.coupon.exception.message.BlankExceptionMessage.ISSUE_QUANTITY_NO_VALUE_EXCEPTION;
import static com.firstone.greenjangteo.coupon.exception.message.InvalidExceptionMessage.INVALID_ISSUE_QUANTITY_EXCEPTION;
import static com.firstone.greenjangteo.coupon.testutil.CouponTestConstant.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class IssueQuantityTest {
    @DisplayName("동일한 발행 매수를 전송하면 동등한 IssueQuantity 인스턴스를 생성한다.")
    @Test
    void ofSameValue() {
        // given, when
        IssueQuantity issueQuantity1 = IssueQuantity.of(ISSUE_QUANTITY1);
        IssueQuantity issueQuantity2 = IssueQuantity.of(ISSUE_QUANTITY1);

        // then
        assertThat(issueQuantity1).isEqualTo(issueQuantity2);
        assertThat(issueQuantity1.hashCode()).isEqualTo(issueQuantity2.hashCode());
    }

    @DisplayName("다른 발행 매수를 전송하면 동등하지 않은 IssueQuantity 인스턴스를 생성한다.")
    @Test
    void ofDifferentValue() {
        // given, when
        IssueQuantity issueQuantity1 = IssueQuantity.of(ISSUE_QUANTITY1);
        IssueQuantity issueQuantity2 = IssueQuantity.of(ISSUE_QUANTITY2);

        // then
        assertThat(issueQuantity1).isNotEqualTo(issueQuantity2);
        assertThat(issueQuantity1.hashCode()).isNotEqualTo(issueQuantity2.hashCode());
    }

    @DisplayName("양의 정수를 전송하면 해당 발행 매수를 가지는 IssueQuantity 인스턴스를 생성한다.")
    @ParameterizedTest
    @CsvSource({"1", "2", "5", "99", "100"})
    void ofPositiveInteger(String issueQuantity) {
        // given, when
        IssueQuantity createdIssueQuantity = IssueQuantity.of(issueQuantity);

        // then
        assertThat(createdIssueQuantity.getValue()).isEqualTo(Integer.parseInt(issueQuantity));
    }

    @DisplayName("-1을 전송하면 발행 매수가 0인 IssueQuantity 인스턴스를 생성한다.")
    @Test
    void ofMinusOne() {
        // given, when
        IssueQuantity createdIssueQuantity = IssueQuantity.of(MINUS_ONE_ISSUE_QUANTITY);

        // then
        assertThat(createdIssueQuantity.getValue()).isEqualTo(0);
    }

    @DisplayName("발행 매수를 전송하지 않으면 IllegalArgumentException이 발생한다.")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void ofBlankValue(String issueQuantity) {
        // when, then
        assertThatThrownBy(() -> IssueQuantity.of(issueQuantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ISSUE_QUANTITY_NO_VALUE_EXCEPTION);
    }

    @DisplayName("유효하지 않은 발행 매수를 전송하면 IllegalArgumentException이 발생한다.")
    @ParameterizedTest
    @CsvSource({"-2", "0", "abc", "ㄱㄴㄷ", "가나다"})
    void ofInvalidValue(String issueQuantity) {
        // given, when, then
        assertThatThrownBy(() -> IssueQuantity.of(issueQuantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(INVALID_ISSUE_QUANTITY_EXCEPTION + issueQuantity);
    }
}