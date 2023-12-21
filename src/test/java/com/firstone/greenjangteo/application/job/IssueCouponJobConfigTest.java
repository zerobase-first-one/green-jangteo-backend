package com.firstone.greenjangteo.application.job;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class IssueCouponJobConfigTest {
    @Autowired
    private Job issueCouponJob;

    @Autowired
    private JobLauncher jobLauncher;

    @DisplayName("Spring Batch를 통해 쿠폰 발행 작업을 수행한다.")
    @Test
    void run() throws Exception {
        // given, when, then
        jobLauncher.run(issueCouponJob, new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters());
    }
}