package com.firstone.greenjangteo.coupon.model;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Objects;

import static com.firstone.greenjangteo.coupon.exception.message.BlankExceptionMessage.ISSUE_QUANTITY_NO_VALUE_EXCEPTION;
import static com.firstone.greenjangteo.coupon.exception.message.InvalidExceptionMessage.INVALID_ISSUE_QUANTITY_EXCEPTION;
import static com.firstone.greenjangteo.utility.RegularExpressionConstant.POSITIVE_INTEGER_OR_MINUS_ONE_PATTERN;

public class IssueQuantity {
    private final int issueQuantity;

    private IssueQuantity(int issueQuantity) {
        this.issueQuantity = issueQuantity;
    }

    public static IssueQuantity of(String issueQuantity) {
        validate(issueQuantity);

        int parsedIssueQuantity = Integer.parseInt(issueQuantity);
        if (parsedIssueQuantity == -1) {
            ++parsedIssueQuantity;
        }

        return new IssueQuantity(parsedIssueQuantity);
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

    public IssueQuantity addQuantity(String issueQuantityToAdd) {
        validate(issueQuantityToAdd);
        return new IssueQuantity(issueQuantity + Integer.parseInt(issueQuantityToAdd));
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
        if (!issueQuantity.matches(POSITIVE_INTEGER_OR_MINUS_ONE_PATTERN)) {
            throw new IllegalArgumentException(INVALID_ISSUE_QUANTITY_EXCEPTION + issueQuantity);
        }
    }

    public boolean isZero() {
        return issueQuantity == 0;
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
