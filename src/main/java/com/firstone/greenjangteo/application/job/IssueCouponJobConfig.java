package com.firstone.greenjangteo.application.job;

import com.firstone.greenjangteo.coupon.model.entity.Coupon;
import com.firstone.greenjangteo.coupon.model.entity.CouponGroup;
import com.firstone.greenjangteo.coupon.repository.CouponGroupRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
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
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static com.firstone.greenjangteo.application.job.utility.LogConstant.UPDATING_COUPON;

@Configuration
@RequiredArgsConstructor
public class IssueCouponJobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final CouponGroupRepository couponGroupRepository;
    private final JdbcTemplate jdbcTemplate;

    private static final Logger log = LoggerFactory.getLogger(IssueCouponJobConfig.class);

    private static final String JOB_NAME = "issueCouponJob";
    private static final String STEP_NAME = "issueCouponStep";
    private static final String COUPON_UPDATE_QUERY
            = "UPDATE coupon SET modified_at = ?, issued_at = ?, expired_at = ? WHERE id = ?";

    @Bean
    public Job issueCouponJob() {
        return jobBuilderFactory
                .get(JOB_NAME)
                .start(issueCouponStep())
                .build();
    }

    @Bean
    public Step issueCouponStep() {
        return stepBuilderFactory
                .get(STEP_NAME)
                .<CouponGroup, List<Coupon>>chunk(10)
                .reader(issueCouponReader())
                .processor(issueCouponProcessor())
                .writer(issueCouponWriter())
                .faultTolerant()
                .retryLimit(10)
                .retry(Exception.class)
                .build();
    }

    public ItemReader<CouponGroup> issueCouponReader() {
        return new CouponGroupItemReader();
    }

    public class CouponGroupItemReader implements ItemReader<CouponGroup> {
        private Iterator<CouponGroup> couponGroupIterator;

        @Override
        public CouponGroup read() {
            if (couponGroupIterator == null) {
                LocalDate today = LocalDate.now();
                List<CouponGroup> couponGroups = couponGroupRepository.findByScheduledIssueDate(today);
                couponGroupIterator = couponGroups.iterator();
            }

            if (couponGroupIterator.hasNext()) {
                return couponGroupIterator.next();
            } else {
                return null;
            }
        }
    }

    public ItemProcessor<CouponGroup, List<Coupon>> issueCouponProcessor() {
        return couponGroup -> {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expirationDateTime = couponGroup.getExpirationPeriod().computeExpirationTime(now);
            return couponGroup.getCoupons().stream()
                    .filter(coupon -> coupon.getIssuedAt() == null)
                    .peek(coupon -> {
                        coupon.issueCoupon(now, expirationDateTime);
                    })
                    .collect(Collectors.toList());
        };
    }

    @Bean
    public ItemWriter<List<Coupon>> issueCouponWriter() {
        return listOfCouponsLists -> {
            for (List<Coupon> coupons : listOfCouponsLists) {
                jdbcTemplate.batchUpdate(
                        COUPON_UPDATE_QUERY,
                        new BatchPreparedStatementSetter() {
                            @Override
                            public void setValues(PreparedStatement ps, int i) throws SQLException {
                                Coupon coupon = coupons.get(i);
                                Long couponId = coupon.getId();

                                log.info(UPDATING_COUPON, couponId);

                                ps.setObject(1, coupon.getModifiedAt());
                                ps.setObject(2, coupon.getIssuedAt());
                                ps.setObject(3, coupon.getExpiredAt());
                                ps.setLong(4, couponId);
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
