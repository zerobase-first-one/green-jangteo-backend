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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@SpringBootTest
class CreateCouponJobConfigTest {
    @Autowired
    private CreateCouponJobConfig createCouponJobConfig;

    @MockBean
    private JobLauncher jobLauncher;

    @MockBean(name = "createCouponJob")
    private Job createCouponJob;

    @DisplayName("쿠폰 생성을 위해 createCouponJob을 실행한다.")
    @Test
    void run() throws Exception {
        // given, when
        jobLauncher.run(createCouponJob, new JobParameters());

        // then
        verify(jobLauncher, times(1)).run(eq(createCouponJob), any(JobParameters.class));
    }

    @DisplayName("JobParameters를 전송해 createCouponJob을 실행한다.")
    @Test
    void runWithParameters() throws Exception {
        // given
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("couponName", "test")
                .addString("amount", "100")
                .addString("description", "test")
                .addString("issueQuantity", "100")
                .addString("scheduledIssueDate", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                .addString("expirationPeriod", "1")
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        // when, then
        jobLauncher.run(createCouponJob, jobParameters);
    }
}
