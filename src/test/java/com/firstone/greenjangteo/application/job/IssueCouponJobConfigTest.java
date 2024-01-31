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
class IssueCouponJobConfigTest {
    @Autowired
    private IssueCouponJobConfig issueCouponJobConfig;

    @MockBean
    private JobLauncher jobLauncher;

    @MockBean(name = "issueCouponJob")
    private Job issueCouponJob;

    @DisplayName("쿠폰 발행을 위해 issueCouponJob을 실행한다.")
    @Test
    void run() throws Exception {
        // given, when
        jobLauncher.run(issueCouponJob, new JobParameters());

        // then
        verify(jobLauncher, times(1)).run(eq(issueCouponJob), any(JobParameters.class));
    }

    @DisplayName("JobParameters를 전송해 issueCouponJob을 실행한다.")
    @Test
    void runWithParameters() throws Exception {
        // given
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        // when, then
        jobLauncher.run(issueCouponJob, jobParameters);
    }
}
