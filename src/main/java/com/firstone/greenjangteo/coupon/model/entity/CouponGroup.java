package com.firstone.greenjangteo.coupon.model.entity;

import com.firstone.greenjangteo.application.model.CouponGroupModel;
import com.firstone.greenjangteo.audit.BaseEntity;
import com.firstone.greenjangteo.coupon.model.Amount;
import com.firstone.greenjangteo.coupon.model.ExpirationPeriod;
import com.firstone.greenjangteo.coupon.model.IssueQuantity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

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

    @Convert(converter = ExpirationPeriod.ExpirationPeriodConverter.class)
    private ExpirationPeriod expirationPeriod;

    @OneToMany(mappedBy = "couponGroup", cascade = {PERSIST, MERGE, REMOVE}, fetch = FetchType.LAZY)
    private List<Coupon> coupons;

    @Builder
    private CouponGroup(String couponName, Amount amount, String description, IssueQuantity issueQuantity,
                        LocalDate scheduledIssueDate, ExpirationPeriod expirationPeriod) {
        this.couponName = couponName;
        this.amount = amount;
        this.description = description;
        this.issueQuantity = issueQuantity;
        remainingQuantity = issueQuantity.getValue();
        this.scheduledIssueDate = scheduledIssueDate;
        this.expirationPeriod = expirationPeriod;
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

    public void addIssueQuantity(String issueQuantityToAdd) {
        issueQuantity = issueQuantity.addQuantity(issueQuantityToAdd);
        remainingQuantity += Integer.parseInt(issueQuantityToAdd);
    }
}
