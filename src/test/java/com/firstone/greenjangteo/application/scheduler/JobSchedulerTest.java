package com.firstone.greenjangteo.application.scheduler;

import com.firstone.greenjangteo.application.job.IssueCouponJobConfig;
import com.firstone.greenjangteo.coupon.model.entity.CouponGroup;
import com.firstone.greenjangteo.coupon.repository.CouponGroupRepository;
import com.firstone.greenjangteo.coupon.testutil.CouponTestObjectFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static com.firstone.greenjangteo.coupon.testutil.CouponTestConstant.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest
class JobSchedulerTest {
    @MockBean
    private JobLauncher jobLauncher;

    @MockBean(name = "issueCouponJob")
    private Job issueCouponJob;

    @MockBean
    private CouponGroupRepository couponGroupRepository;

    @Autowired
    private IssueCouponJobConfig issueCouponJobConfig;

    @DisplayName("쿠폰 발행을 위해 issueCouponJob을 실행한다.")
    @Test
    void runIssueCouponJobTest() throws Exception {
        // given
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        CouponGroup createdCouponGroup1
                = CouponTestObjectFactory.createCouponGroup(
                COUPON_NAME1, AMOUNT, DESCRIPTION, ISSUE_QUANTITY, tomorrow, EXPIRATION_PERIOD
        );

        CouponGroup createdCouponGroup2
                = CouponTestObjectFactory.createCouponGroup(
                COUPON_NAME1, AMOUNT, DESCRIPTION, ISSUE_QUANTITY, tomorrow, EXPIRATION_PERIOD
        );

        List<CouponGroup> couponGroups = List.of(createdCouponGroup1, createdCouponGroup2);

        when(couponGroupRepository.findByScheduledIssueDate(any(LocalDate.class)))
                .thenReturn(couponGroups);

        Job jobToRun = issueCouponJobConfig.issueCouponJob();

        // when
        jobLauncher.run(jobToRun, new JobParameters());

        // then
        verify(jobLauncher, times(1)).run(eq(issueCouponJob), any(JobParameters.class));
    }
}