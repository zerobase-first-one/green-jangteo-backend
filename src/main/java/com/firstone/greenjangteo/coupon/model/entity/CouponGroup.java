package com.firstone.greenjangteo.coupon.model.entity;

import com.firstone.greenjangteo.application.model.CouponGroupModel;
import com.firstone.greenjangteo.audit.BaseEntity;
import com.firstone.greenjangteo.coupon.dto.request.IssueCouponsRequestDto;
import com.firstone.greenjangteo.coupon.exception.serious.InconsistentCouponSizeException;
import com.firstone.greenjangteo.coupon.exception.serious.InsufficientRemainingQuantityException;
import com.firstone.greenjangteo.coupon.model.Amount;
import com.firstone.greenjangteo.coupon.model.ExpirationPeriod;
import com.firstone.greenjangteo.coupon.model.IssueQuantity;
import com.firstone.greenjangteo.user.model.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.firstone.greenjangteo.coupon.exception.message.AbnormalStateExceptionMessage.*;
import static javax.persistence.CascadeType.*;

@Entity(name = "coupon_group")
@Table(name = "coupon_group")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CouponGroup extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String couponName;

    @Convert(converter = Amount.AmountConverter.class)
    @Column(nullable = false)
    private Amount amount;

    @Column(nullable = false)
    private String description;

    @Convert(converter = IssueQuantity.IssueQuantityConverter.class)
    @Column(nullable = false)
    private IssueQuantity issueQuantity;

    private int remainingQuantity;

    @Column(nullable = false)
    private LocalDate scheduledIssueDate;

    private boolean isImmediateProvisionRequired;

    @Convert(converter = ExpirationPeriod.ExpirationPeriodConverter.class)
    private ExpirationPeriod expirationPeriod;

    @OneToMany(mappedBy = "couponGroup", cascade = {PERSIST, MERGE, REMOVE}, fetch = FetchType.LAZY)
    private List<Coupon> coupons;

    @Builder
    private CouponGroup(String couponName, Amount amount, String description, IssueQuantity issueQuantity,
                        LocalDate scheduledIssueDate, boolean isImmediateProvisionRequired, ExpirationPeriod expirationPeriod) {
        this.couponName = couponName;
        this.amount = amount;
        this.description = description;
        this.issueQuantity = issueQuantity;
        remainingQuantity = issueQuantity.getValue();
        this.scheduledIssueDate = scheduledIssueDate;
        this.isImmediateProvisionRequired = isImmediateProvisionRequired;
        this.expirationPeriod = expirationPeriod;
    }

    public static CouponGroup from
            (IssueCouponsRequestDto issueCouponsRequestDto, boolean isImmediateProvisionRequired) {
        return CouponGroup.builder()
                .couponName(issueCouponsRequestDto.getCouponName())
                .amount(Amount.of(issueCouponsRequestDto.getAmount()))
                .description(issueCouponsRequestDto.getDescription())
                .issueQuantity(IssueQuantity.of(issueCouponsRequestDto.getIssueQuantity()))
                .scheduledIssueDate(issueCouponsRequestDto.getScheduledIssueDate())
                .isImmediateProvisionRequired(isImmediateProvisionRequired)
                .expirationPeriod(ExpirationPeriod.of(issueCouponsRequestDto.getExpirationPeriod()))
                .build();
    }

    public static CouponGroup from(CouponGroupModel couponGroupModel) {
        return CouponGroup.builder()
                .couponName(couponGroupModel.getCouponName())
                .amount(Amount.of(couponGroupModel.getAmount()))
                .description(couponGroupModel.getDescription())
                .issueQuantity(IssueQuantity.of(couponGroupModel.getIssueQuantity()))
                .scheduledIssueDate(couponGroupModel.getScheduledIssueDate())
                .expirationPeriod(ExpirationPeriod.of(couponGroupModel.getExpirationPeriod()))
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CouponGroup that = (CouponGroup) o;
        return remainingQuantity == that.remainingQuantity && Objects.equals(id, that.id)
                && Objects.equals(couponName, that.couponName) && Objects.equals(amount, that.amount)
                && Objects.equals(description, that.description) && Objects.equals(issueQuantity, that.issueQuantity)
                && Objects.equals(scheduledIssueDate, that.scheduledIssueDate)
                && Objects.equals(expirationPeriod, that.expirationPeriod) && Objects.equals(coupons, that.coupons);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, couponName, amount, description, issueQuantity,
                remainingQuantity, scheduledIssueDate, expirationPeriod, coupons);
    }

    public boolean isIssueToAllUsersRequired() {
        return issueQuantity.isZero() && isImmediateProvisionRequired;
    }

    public void addIssueQuantity(String issueQuantityToAdd) {
        issueQuantity = issueQuantity.addQuantity(issueQuantityToAdd);
        remainingQuantity += Integer.parseInt(issueQuantityToAdd);
    }

    public boolean isCouponsRemained(int requiredQuantity) {
        return remainingQuantity >= requiredQuantity;
    }

    public void reduceRemainingQuantity(int assignedQuantity) {
        remainingQuantity -= assignedQuantity;
    }

    public void addInsufficientCoupons(int requiredQuantity) {
        LocalDateTime now = LocalDateTime.now();
        int quantityToIssue = requiredQuantity - remainingQuantity;
        for (int i = 0; i < quantityToIssue; i++) {
            coupons.add(new Coupon(this, now));
        }

        addIssueQuantity(String.valueOf(quantityToIssue));
    }

    public void issueAndAddUserToCoupons(User user, List<Coupon> coupons, int requiredQuantity) {
        int couponsSize = coupons.size();
        validateCouponsSize(couponsSize, requiredQuantity);
        validateRemainingQuantity(remainingQuantity, couponsSize);

        for (Coupon coupon : coupons) {
            coupon.issueAndAddUser(user, expirationPeriod);
        }
        reduceRemainingQuantity(requiredQuantity);
    }

    public List<Coupon> getUnassignedCoupons() {
        return coupons.stream()
                .filter(coupon -> coupon.getUser() == null)
                .collect(Collectors.toList());
    }

    private void validateCouponsSize(int size, int requiredQuantity) {
        if (size != requiredQuantity) {
            throw new InconsistentCouponSizeException(INCONSISTENT_COUPON_SIZE_EXCEPTION + size
                    + INCONSISTENT_COUPON_SIZE_EXCEPTION_REQUIRED_QUANTITY + requiredQuantity);
        }
    }

    private void validateRemainingQuantity(int remainingQuantity, int size) {
        if (remainingQuantity < size) {
            throw new InsufficientRemainingQuantityException
                    (INSUFFICIENT_REMAINING_QUANTITY_EXCEPTION + remainingQuantity
                            + INSUFFICIENT_REMAINING_QUANTITY_EXCEPTION_QUANTITY_TO_PROVIDE + size);
        }
    }
}
