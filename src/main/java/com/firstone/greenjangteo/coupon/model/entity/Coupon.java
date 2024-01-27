package com.firstone.greenjangteo.coupon.model.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.firstone.greenjangteo.coupon.exception.serious.AlreadyProvidedCouponException;
import com.firstone.greenjangteo.coupon.exception.serious.AlreadyUsedCouponException;
import com.firstone.greenjangteo.coupon.exception.serious.NotUsedCouponException;
import com.firstone.greenjangteo.coupon.model.ExpirationPeriod;
import com.firstone.greenjangteo.user.model.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

import static com.firstone.greenjangteo.coupon.exception.message.AbnormalStateExceptionMessage.*;

@Entity(name = "coupon")
@Table(name = "coupon")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long usedOrderId;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime modifiedAt;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime issuedAt;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime expiredAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_group_id")
    private CouponGroup couponGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Coupon(CouponGroup couponGroup, LocalDateTime createdAt) {
        this.couponGroup = couponGroup;
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coupon coupon = (Coupon) o;
        return Objects.equals(id, coupon.id) && Objects.equals(user, coupon.user)
                && Objects.equals(usedOrderId, coupon.usedOrderId)
                && Objects.equals(couponGroup, coupon.couponGroup) && Objects.equals(createdAt, coupon.createdAt)
                && Objects.equals(modifiedAt, coupon.modifiedAt) && Objects.equals(issuedAt, coupon.issuedAt)
                && Objects.equals(expiredAt, coupon.expiredAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, usedOrderId, couponGroup, createdAt, modifiedAt, issuedAt, expiredAt);
    }

    public void addUser(User user) {
        validateUserIsNull();
        this.user = user;
    }

    public void issueAndAddUser(User user, ExpirationPeriod expirationPeriod) {
        LocalDateTime now = LocalDateTime.now();
        issueCoupon(now, expirationPeriod.computeExpirationTime(now));

        addUser(user);
    }

    public void issueCoupon(LocalDateTime now, LocalDateTime expirationTime) {
        modifiedAt = now;
        issuedAt = now;
        expiredAt = expirationTime;
    }

    private void validateUserIsNull() {
        if (user == null) {
            return;
        }

        throw new AlreadyProvidedCouponException(ALREADY_GIVEN_COUPON_EXCEPTION + user.getId());
    }

    public void addOrderId(Long orderId) {
        if (usedOrderId == null) {
            usedOrderId = orderId;
            return;
        }

        throw new AlreadyUsedCouponException(ALREADY_USED_COUPON_EXCEPTION + usedOrderId);
    }

    public void removeOrderId(Long orderId) {
        if (usedOrderId == null) {
            throw new NotUsedCouponException(NOT_USED_COUPON_EXCEPTION);
        }

        usedOrderId = null;
    }
}
