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
class ProvideCouponJobConfigTest {
    @Autowired
    private ProvideCouponJobConfig provideCouponJobConfig;

    @MockBean
    private JobLauncher jobLauncher;

    @MockBean(name = "provideCouponJob")
    private Job provideCouponJob;

    @DisplayName("쿠폰 지급을 위해 provideCouponJob을 실행한다.")
    @Test
    void run() throws Exception {
        // given, when
        jobLauncher.run(provideCouponJob, new JobParameters());

        // then
        verify(jobLauncher, times(1)).run(eq(provideCouponJob), any(JobParameters.class));
    }

    @DisplayName("JobParameters를 전송해 provideCouponJob을 실행한다.")
    @Test
    void runWithParameters() throws Exception {
        // given
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("couponGroupId", 1L)
                .addString("userIds", "1,2,3,4,5")
                .addLong("quantity", 1L)
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        // when, then
        jobLauncher.run(provideCouponJob, jobParameters);
    }
}