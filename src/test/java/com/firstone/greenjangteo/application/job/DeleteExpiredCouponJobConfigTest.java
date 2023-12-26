package com.firstone.greenjangteo.application.job;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@SpringBootTest
class DeleteExpiredCouponJobConfigTest {
    @Autowired
    private DeleteExpiredCouponJobConfig deleteExpiredCouponJobConfig;

    @MockBean
    private JobLauncher jobLauncher;

    @MockBean(name = "issueCouponJob")
    private Job deleteExpiredCouponJob;

    @DisplayName("만료된 쿠폰 삭제를 위해 deleteExpiredCouponJob을 실행한다.")
    @Test
    void run() throws Exception {
        // given, when
        jobLauncher.run(deleteExpiredCouponJob, new JobParameters());

        // then
        verify(jobLauncher, times(1)).run(eq(deleteExpiredCouponJob), any(JobParameters.class));
    }

    @DisplayName("JobParameters를 전송해 deleteExpiredCouponJob을 실행한다.")
    @Test
    void runWithParameters() throws Exception {
        // given
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        // when, then
        jobLauncher.run(deleteExpiredCouponJob, jobParameters);
    }
}