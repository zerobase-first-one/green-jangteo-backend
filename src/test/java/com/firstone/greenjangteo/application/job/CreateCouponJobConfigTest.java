package com.firstone.greenjangteo.application.job;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@ActiveProfiles("test")
@SpringBootTest
class CreateCouponJobConfigTest {
    @Autowired
    private Job createCouponJob;

    @Autowired
    private JobLauncher jobLauncher;

    @DisplayName("Spring Batch를 통해 쿠폰 생성 작업을 수행한다.")
    @Test
    void run() throws Exception {
        // given, when, then
        jobLauncher.run(createCouponJob, new JobParametersBuilder()
                .addString("couponName", "test")
                .addString("amount", "100")
                .addString("description", "test")
                .addString("issueQuantity", "100")
                .addString("scheduledIssueDate", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                .addString("expirationPeriod", "1")
                .addLong("time", System.currentTimeMillis())
                .toJobParameters());
    }
}
