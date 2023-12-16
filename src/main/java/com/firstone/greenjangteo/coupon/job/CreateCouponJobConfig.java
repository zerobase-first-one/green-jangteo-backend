package com.firstone.greenjangteo.coupon.job;

import com.firstone.greenjangteo.coupon.model.CouponGroupModel;
import com.firstone.greenjangteo.coupon.model.entity.Coupon;
import com.firstone.greenjangteo.coupon.model.entity.CouponGroup;
import com.firstone.greenjangteo.coupon.repository.CouponGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class CreateCouponJobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final CouponGroupRepository couponGroupRepository;
    private final JdbcTemplate jdbcTemplate;

    @Bean
    public Job createCouponJob() {
        return jobBuilderFactory.get("createCouponJob")
                .incrementer(new RunIdIncrementer())
                .start(createCouponStep())
                .build();
    }

    @Bean
    public Step createCouponStep() {
        return stepBuilderFactory.get("createCouponStep")
                .<CouponGroupModel, List<Coupon>>chunk(100)
                .reader(createCouponReader())
                .processor(createCouponProcessor())
                .writer(createCouponWriter())
                .build();
    }

    @Bean
    public ItemReader<CouponGroupModel> createCouponReader() {
        return new CouponItemReader();
    }

    public static class CouponItemReader implements ItemReader<CouponGroupModel>, StepExecutionListener {
        private CouponGroupModel couponGroupModel;
        private boolean batchJobState = false;

        @Override
        public void beforeStep(StepExecution stepExecution) {
            long scheduledIssueDateMillis = stepExecution.getJobParameters().getLong("scheduledIssueDate");

            String couponName = stepExecution.getJobParameters().getString("couponName");
            String amount = stepExecution.getJobParameters().getString("amount");
            String description = stepExecution.getJobParameters().getString("description");
            String issueQuantity = stepExecution.getJobParameters().getString("issueQuantity");
            LocalDateTime scheduledIssueDate
                    = LocalDateTime.ofInstant(Instant.ofEpochMilli(scheduledIssueDateMillis), ZoneId.systemDefault());
            String expirationPeriod = stepExecution.getJobParameters().getString("expirationPeriod");

            couponGroupModel = CouponGroupModel.builder()
                    .couponName(couponName)
                    .amount(amount)
                    .description(description)
                    .issueQuantity(issueQuantity)
                    .scheduledIssueDate(scheduledIssueDate)
                    .expirationPeriod(expirationPeriod)
                    .build();
        }

        @Override
        public CouponGroupModel read() {
            if (!batchJobState) {
                batchJobState = true;
                return this.couponGroupModel;
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
            CouponGroup couponGroup = couponGroupRepository.findByCouponName(couponGroupModel.getCouponName())
                    .orElseGet(() -> createCouponGroup(couponGroupModel));

            List<Coupon> coupons = new ArrayList<>();
            for (int i = 0; i < Integer.parseInt(couponGroupModel.getIssueQuantity()); i++) {
                coupons.add(new Coupon(couponGroup, LocalDateTime.now()));
            }
            return coupons;
        };
    }

    private CouponGroup createCouponGroup(CouponGroupModel couponGroupModel) {
        CouponGroup couponGroup = couponGroupRepository.findByCouponName(couponGroupModel.getCouponName())
                .orElse(CouponGroup.from(couponGroupModel));

        return couponGroupRepository.save(couponGroup);
    }

    @Bean
    public ItemWriter<List<Coupon>> createCouponWriter() {
        return listOfCouponsLists -> {
            for (List<Coupon> coupons : listOfCouponsLists) {
                jdbcTemplate.batchUpdate(
                        "INSERT INTO coupon (coupon_group_id, created_at) VALUES (?, ?)",
                        new BatchPreparedStatementSetter() {
                            @Override
                            public void setValues(PreparedStatement ps, int i) throws SQLException {
                                Coupon coupon = coupons.get(i);
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
}
