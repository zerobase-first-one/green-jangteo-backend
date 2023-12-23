package com.firstone.greenjangteo.coupon.service;

import com.firstone.greenjangteo.coupon.dto.IssueCouponsRequestDto;
import com.firstone.greenjangteo.coupon.dto.ProvideCouponsToUserRequestDto;
import com.firstone.greenjangteo.coupon.model.entity.Coupon;
import com.firstone.greenjangteo.coupon.model.entity.CouponGroup;
import com.firstone.greenjangteo.coupon.repository.CouponGroupRepository;
import com.firstone.greenjangteo.coupon.repository.CouponRepository;
import com.firstone.greenjangteo.user.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.firstone.greenjangteo.coupon.excpeption.message.NotFoundExceptionMessage.COUPON_NAME_NOT_FOUND_EXCEPTION;
import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {
    private final JobLauncher jobLauncher;
    private final Job createCouponJob;
    private final CouponGroupRepository couponGroupRepository;
    private final CouponRepository couponRepository;

    private static final Logger log = LoggerFactory.getLogger(CouponService.class);

    private static final String QUANTITY_SHORTAGE_MESSAGE = "couponsRemained: {} is less than requiredQuantity: {}";

    @Override
    public void createCoupons(IssueCouponsRequestDto issueCouponsRequestDto) throws JobExecutionException {
        String scheduledIssueDate = issueCouponsRequestDto.getScheduledIssueDate()
                .format(DateTimeFormatter.ISO_LOCAL_DATE);

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("couponName", issueCouponsRequestDto.getCouponName())
                .addString("amount", issueCouponsRequestDto.getAmount())
                .addString("description", issueCouponsRequestDto.getDescription())
                .addString("issueQuantity", issueCouponsRequestDto.getIssueQuantity())
                .addString("scheduledIssueDate", scheduledIssueDate)
                .addString("expirationPeriod", issueCouponsRequestDto.getExpirationPeriod())
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        jobLauncher.run(createCouponJob, jobParameters);
    }

    @Override
    public void provideCouponsToUser(ProvideCouponsToUserRequestDto provideCouponsToUserRequestDto) {
        CouponGroup couponGroup;
        try {
            couponGroup = getCouponGroup(provideCouponsToUserRequestDto.getCouponName());
        } catch (EntityNotFoundException e) {
            log.warn(e.getMessage());
            return;
        }

        int requiredQuantity = provideCouponsToUserRequestDto.getQuantity();

        if (!couponGroup.isCouponsRemained(requiredQuantity)) {
            log.warn(QUANTITY_SHORTAGE_MESSAGE, couponGroup.getRemainingQuantity(), requiredQuantity);
            couponGroup.addInsufficientCoupons(requiredQuantity);
        }

        User user = provideCouponsToUserRequestDto.getUser();

        addUserToCoupons(user, couponGroup, requiredQuantity);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED, readOnly = true, timeout = 10)
    public CouponGroup getCouponGroup(String couponName) {
        return couponGroupRepository.findByCouponName(couponName)
                .orElseThrow(() -> new EntityNotFoundException(COUPON_NAME_NOT_FOUND_EXCEPTION + couponName));
    }

    private void addUserToCoupons(User user, CouponGroup couponGroup, int requiredQuantity) {
        Pageable limit = PageRequest.of(0, requiredQuantity);
        List<Coupon> coupons
                = couponRepository.findByCouponGroupAndUserIsNull(couponGroup, limit);
        couponGroup.addUserToCoupons(user, coupons, requiredQuantity);

        couponRepository.saveAll(coupons);
    }
}
