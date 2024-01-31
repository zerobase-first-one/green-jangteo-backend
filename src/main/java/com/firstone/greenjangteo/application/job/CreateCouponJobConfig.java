package com.firstone.greenjangteo.application.job;

import com.firstone.greenjangteo.application.model.CouponGroupModel;
import com.firstone.greenjangteo.coupon.model.entity.Coupon;
import com.firstone.greenjangteo.coupon.model.entity.CouponGroup;
import com.firstone.greenjangteo.coupon.repository.CouponGroupRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.firstone.greenjangteo.application.job.utility.LogConstant.UPDATING_COUPON;

@Configuration
@RequiredArgsConstructor
public class CreateCouponJobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final CouponGroupRepository couponGroupRepository;
    private final JdbcTemplate jdbcTemplate;

    private static final Logger log = LoggerFactory.getLogger(CreateCouponJobConfig.class);

    private static final String JOB_NAME = "createCouponJob";
    private static final String STEP_NAME = "createCouponStep";
    private static final String PREPARING_BEFORE_STEP = "Preparing beforeStep";
    private static final String COUPON_INSERT_QUERY = "INSERT INTO coupon (coupon_group_id, created_at) VALUES (?, ?)";

    @Bean
    public Job createCouponJob() {
        return jobBuilderFactory
                .get(JOB_NAME)
                .start(createCouponStep())
                .build();
    }

    @Bean
    public Step createCouponStep() {
        return stepBuilderFactory
                .get(STEP_NAME)
                .<CouponGroupModel, List<Coupon>>chunk(100) // 비교적 단순한 작업이므로 100으로 설정
                .reader(createCouponReader())
                .processor(createCouponProcessor())
                .writer(createCouponWriter())
                .faultTolerant()
                .retryLimit(10)
                .retry(Exception.class)
                .build();
    }

    @Bean
    public ItemReader<CouponGroupModel> createCouponReader() {
        return new CouponItemReader();
    }

    public static class CouponItemReader implements ItemReader<CouponGroupModel>, StepExecutionListener {
        private CouponGroupModel couponGroupModel;
        private boolean batchJobState;

        @Override
        public void beforeStep(StepExecution stepExecution) {
            log.info(PREPARING_BEFORE_STEP);
            String couponName = stepExecution.getJobParameters().getString("couponName");
            String amount = stepExecution.getJobParameters().getString("amount");
            String description = stepExecution.getJobParameters().getString("description");
            String issueQuantity = stepExecution.getJobParameters().getString("issueQuantity");

            LocalDate scheduledIssueDate = parseScheduledIssueDateToLocalDate(
                    stepExecution.getJobParameters().getString("scheduledIssueDate")
            );

            String expirationPeriod = stepExecution.getJobParameters().getString("expirationPeriod");

            couponGroupModel = CouponGroupModel.builder()
                    .couponName(couponName)
                    .amount(amount)
                    .description(description)
                    .issueQuantity(issueQuantity)
                    .scheduledIssueDate(scheduledIssueDate)
                    .expirationPeriod(expirationPeriod)
                    .build();

            batchJobState = false;
        }

        @Override
        public CouponGroupModel read() {
            if (!batchJobState) {
                batchJobState = true;
                return couponGroupModel;
            }
            return null;
        }

        @Override
        public ExitStatus afterStep(StepExecution stepExecution) {
            return ExitStatus.COMPLETED;
        }
    }


    @Bean
    public ItemProcessor<CouponGroupModel, List<Coupon>> createCouponProcessor() {
        return couponGroupModel -> {
            CouponGroup couponGroup = createOrUpdateCouponGroup(couponGroupModel);

            List<Coupon> coupons = new ArrayList<>();
            for (int i = 0; i < Integer.parseInt(couponGroupModel.getIssueQuantity()); i++) {
                coupons.add(new Coupon(couponGroup, LocalDateTime.now()));
            }
            return coupons;
        };
    }

    @Bean
    public ItemWriter<List<Coupon>> createCouponWriter() {
        return listOfCouponsLists -> {
            for (List<Coupon> coupons : listOfCouponsLists) {
                jdbcTemplate.batchUpdate(
                        COUPON_INSERT_QUERY,
                        new BatchPreparedStatementSetter() {
                            @Override
                            public void setValues(PreparedStatement ps, int i) throws SQLException {
                                Coupon coupon = coupons.get(i);
                                log.info(UPDATING_COUPON, coupon.getId());

                                ps.setLong(1, coupon.getCouponGroup().getId());
                                ps.setObject(2, coupon.getCreatedAt());
                            }

                            @Override
                            public int getBatchSize() {
                                return coupons.size();
                            }
                        }
                );
            }
        };
    }

    private static LocalDate parseScheduledIssueDateToLocalDate(String stringValue) {
        return LocalDate.parse(stringValue, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    private CouponGroup createOrUpdateCouponGroup(CouponGroupModel couponGroupModel) {
        Optional<CouponGroup> existingCouponGroup
                = couponGroupRepository.findByCouponName(couponGroupModel.getCouponName());

        if (existingCouponGroup.isPresent()) {
            CouponGroup couponGroup = existingCouponGroup.get();
            couponGroup.addIssueQuantity(couponGroupModel.getIssueQuantity());
            return couponGroupRepository.save(couponGroup);
        }

        CouponGroup couponGroup = CouponGroup.from(couponGroupModel);
        return couponGroupRepository.save(couponGroup);
    }
}
