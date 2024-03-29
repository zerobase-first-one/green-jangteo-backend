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

import static com.firstone.greenjangteo.coupon.exception.message.NotFoundExceptionMessage.COUPON_GROUP_ID_NOT_FOUND_EXCEPTION;
import static com.firstone.greenjangteo.coupon.exception.message.NotFoundExceptionMessage.COUPON_NAME_NOT_FOUND_EXCEPTION;
import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;

@Service
@RequiredArgsConstructor
public class CouponGroupServiceImpl implements CouponGroupService {
    private final CouponGroupRepository couponGroupRepository;
    private final CouponRepository couponRepository;

    @Override
    @Transactional(isolation = READ_COMMITTED, timeout = 10)
    public void addCouponGroupToImmediatelyIssue(IssueCouponsRequestDto issueCouponsRequestDto) {
        CouponGroup couponGroup = CouponGroup.from(issueCouponsRequestDto, true);
        couponGroupRepository.save(couponGroup);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED, readOnly = true, timeout = 15)
    public List<CouponGroup> getCouponGroups() {
        return couponGroupRepository.findAll();
    }

    @Override
    @Transactional(isolation = READ_COMMITTED, readOnly = true, timeout = 10)
    public Page<Coupon> getCouponGroup(Pageable pageable, Long couponGroupId) {
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
    @Transactional(isolation = READ_COMMITTED, timeout = 10)
    public void deleteCouponGroup(Long couponGroupId) {
        if (couponGroupRepository.existsById(couponGroupId)) {
            couponGroupRepository.deleteById(couponGroupId);
            return;
        }

        throw new EntityNotFoundException(COUPON_GROUP_ID_NOT_FOUND_EXCEPTION + couponGroupId);
    }
}
