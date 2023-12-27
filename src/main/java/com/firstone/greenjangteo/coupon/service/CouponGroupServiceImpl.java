package com.firstone.greenjangteo.coupon.service;

import com.firstone.greenjangteo.coupon.dto.request.IssueCouponsRequestDto;
import com.firstone.greenjangteo.coupon.model.entity.Coupon;
import com.firstone.greenjangteo.coupon.model.entity.CouponGroup;
import com.firstone.greenjangteo.coupon.repository.CouponGroupRepository;
import com.firstone.greenjangteo.coupon.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static com.firstone.greenjangteo.coupon.excpeption.message.NotFoundExceptionMessage.COUPON_GROUP_ID_NOT_FOUND_EXCEPTION;
import static com.firstone.greenjangteo.coupon.excpeption.message.NotFoundExceptionMessage.COUPON_NAME_NOT_FOUND_EXCEPTION;
import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;

@Service
@RequiredArgsConstructor
@Transactional(isolation = READ_COMMITTED, readOnly = true, timeout = 10)
public class CouponGroupServiceImpl implements CouponGroupService {
    private final CouponGroupRepository couponGroupRepository;
    private final CouponRepository couponRepository;

    @Override
    public void addCouponGroupToImmediatelyIssue(IssueCouponsRequestDto issueCouponsRequestDto) {
        CouponGroup couponGroup = CouponGroup.from(issueCouponsRequestDto, true);
        couponGroupRepository.save(couponGroup);
    }

    @Override
    public List<CouponGroup> getCouponGroups() {
        return couponGroupRepository.findAll();
    }

    @Override
    public Page<Coupon> getCouponGroup(Long couponGroupId, Pageable pageable) {
        if (couponGroupRepository.existsById(couponGroupId)) {
            return couponRepository.findByCouponGroupId(couponGroupId, pageable);
        }

        throw new EntityNotFoundException(COUPON_GROUP_ID_NOT_FOUND_EXCEPTION + couponGroupId);
    }

    @Override
    public CouponGroup getCouponGroup(String couponName) {
        return couponGroupRepository.findByCouponName(couponName)
                .orElseThrow(() -> new EntityNotFoundException(COUPON_NAME_NOT_FOUND_EXCEPTION + couponName));
    }

    @Override
    public void deleteCouponGroup(Long couponGroupId) {
        if (couponGroupRepository.existsById(couponGroupId)) {
            couponGroupRepository.deleteById(couponGroupId);
            return;
        }

        throw new EntityNotFoundException(COUPON_GROUP_ID_NOT_FOUND_EXCEPTION + couponGroupId);
    }
}
