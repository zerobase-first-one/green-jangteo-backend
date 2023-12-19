package com.firstone.greenjangteo.coupon.model;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Objects;

import static com.firstone.greenjangteo.coupon.excpeption.message.BlankExceptionMessage.ISSUE_QUANTITY_NO_VALUE_EXCEPTION;
import static com.firstone.greenjangteo.coupon.excpeption.message.InvalidExceptionMessage.INVALID_ISSUE_QUANTITY_EXCEPTION;
import static com.firstone.greenjangteo.utility.RegularExpressionConstant.POSITIVE_INTEGER_PATTERN;

public class IssueQuantity {
    private int issueQuantity;

    private IssueQuantity(int issueQuantity) {
        this.issueQuantity = issueQuantity;
    }

    public static IssueQuantity of(String issueQuantity) {
        validate(issueQuantity);
        return new IssueQuantity(Integer.parseInt(issueQuantity));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IssueQuantity that = (IssueQuantity) o;
        return issueQuantity == that.issueQuantity;
    }

    @Override
    public int hashCode() {
        return Objects.hash(issueQuantity);
    }

    public int getValue() {
        return issueQuantity;
    }

    private static void validate(String issueQuantity) {
        checkIssueQuantityIsNotBlank(issueQuantity);
        checkIssueQuantityPattern(issueQuantity);
    }

    private static void checkIssueQuantityIsNotBlank(String issueQuantity) {
        if (issueQuantity == null || issueQuantity.isBlank()) {
            throw new IllegalArgumentException(ISSUE_QUANTITY_NO_VALUE_EXCEPTION);
        }
    }

    private static void checkIssueQuantityPattern(String issueQuantity) {
        if (!issueQuantity.matches(POSITIVE_INTEGER_PATTERN)) {
            throw new IllegalArgumentException(INVALID_ISSUE_QUANTITY_EXCEPTION + issueQuantity);
        }
    }

    @Converter
    public static class IssueQuantityConverter implements AttributeConverter<IssueQuantity, Integer> {
        @Override
        public Integer convertToDatabaseColumn(IssueQuantity issueQuantity) {
            return issueQuantity.issueQuantity;
        }

        @Override
        public IssueQuantity convertToEntityAttribute(Integer issueQuantity) {
            return issueQuantity == null ? null : new IssueQuantity(issueQuantity);
        }
    }
}
