package com.firstone.greenjangteo.coupon.service;

import com.firstone.greenjangteo.coupon.dto.IssueCouponsRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {
    private final JobLauncher jobLauncher;
    private final Job createCouponJob;

    public void createCoupons(IssueCouponsRequestDto issueCouponsRequestDto) throws JobExecutionException {
        long scheduledIssueDateMillis
                = serializeLocalDateTimeToTimeStamp(issueCouponsRequestDto.getScheduledIssueDate());

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("couponName", issueCouponsRequestDto.getCouponName())
                .addString("amount", issueCouponsRequestDto.getAmount())
                .addString("description", issueCouponsRequestDto.getDescription())
                .addString("issueQuantity", issueCouponsRequestDto.getIssueQuantity())
                .addLong("scheduledIssueDate", scheduledIssueDateMillis)
                .addString("expirationPeriod", issueCouponsRequestDto.getExpirationPeriod())
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        jobLauncher.run(createCouponJob, jobParameters);
    }

    private long serializeLocalDateTimeToTimeStamp(LocalDateTime scheduledIssueDate) {
        return scheduledIssueDate
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
    }
}
