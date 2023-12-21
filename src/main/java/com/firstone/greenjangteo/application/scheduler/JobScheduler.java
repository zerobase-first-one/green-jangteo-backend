package com.firstone.greenjangteo.application.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class JobScheduler {
    private final Job issueCouponJob;
    private final JobLauncher jobLauncher;

    private static final String CRON_MIDNIGHT_EXPRESSION = "0 0 0 * * *";
    private static final String SCHEDULED_ISSUE_JOB_START = "Beginning to scheduled issuing coupon job";

    /**
     * 매일 자정에 실행
     */
    @Scheduled(cron = CRON_MIDNIGHT_EXPRESSION)
    public void runIssueCouponJob() throws JobExecutionException {
        log.info(SCHEDULED_ISSUE_JOB_START);
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(issueCouponJob, jobParameters);
    }
}
