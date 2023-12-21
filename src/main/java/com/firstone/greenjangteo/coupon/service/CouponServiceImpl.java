package com.firstone.greenjangteo.coupon.service;

import com.firstone.greenjangteo.coupon.dto.IssueCouponsRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {
    private final JobLauncher jobLauncher;
    private final Job createCouponJob;

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
}
