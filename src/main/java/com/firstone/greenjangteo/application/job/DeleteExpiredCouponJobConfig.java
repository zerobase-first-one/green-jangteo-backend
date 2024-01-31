package com.firstone.greenjangteo.application.job;

import com.firstone.greenjangteo.coupon.model.entity.Coupon;
import com.firstone.greenjangteo.coupon.model.entity.CouponGroup;
import com.firstone.greenjangteo.coupon.repository.CouponGroupRepository;
import com.firstone.greenjangteo.coupon.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
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
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.firstone.greenjangteo.application.job.utility.LogConstant.UPDATING_COUPON;

@Configuration
@RequiredArgsConstructor
public class DeleteExpiredCouponJobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final CouponGroupRepository couponGroupRepository;
    private final CouponRepository couponRepository;
    private final JdbcTemplate jdbcTemplate;

    private static final Logger log = LoggerFactory.getLogger(DeleteExpiredCouponJobConfig.class);

    private static final String JOB_NAME = "deleteExpiredCouponsJob";
    private static final String STEP_NAME = "deleteExpiredCouponsStep";
    private static final String COUPON_DELETE_QUERY = "DELETE FROM coupon WHERE id = ?";

    @Bean
    public Job deleteExpiredCouponJob() {
        return jobBuilderFactory
                .get(JOB_NAME)
                .start(deleteExpiredCouponStep())
                .build();
    }

    @Bean
    public Step deleteExpiredCouponStep() {
        return stepBuilderFactory
                .get(STEP_NAME)
                .<Coupon, Coupon>chunk(1_000) // 긴급성이 낮은 작업이므로 Coupon 단위 1,000으로 설정
                .reader(expiredCouponReader())
                .writer(expiredCouponWriter())
                .faultTolerant()
                .retryLimit(10)
                .retry(Exception.class)
                .build();
    }

    public ItemReader<Coupon> expiredCouponReader() {
        return new ExpiredCouponReader(couponRepository);
    }

    public class ExpiredCouponReader implements ItemReader<Coupon> {
        private final CouponRepository couponRepository;
        private Iterator<Coupon> couponIterator;

        public ExpiredCouponReader(CouponRepository couponRepository) {
            this.couponRepository = couponRepository;
        }

        @Override
        public Coupon read() {
            if (couponIterator == null) {
                LocalDateTime endOfToday = LocalDate.now().atTime(LocalTime.MAX);
                List<Coupon> expiredCoupons = couponRepository.findByExpiredAtBefore(endOfToday);
                couponIterator = expiredCoupons.iterator();
            }

            if (couponIterator.hasNext()) {
                return couponIterator.next();
            }

            return null;
        }
    }

    @Bean
    public ItemWriter<Coupon> expiredCouponWriter() {
        return coupons -> {
            jdbcTemplate.batchUpdate(
                    COUPON_DELETE_QUERY,
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            Long couponId = coupons.get(i).getId();

                            log.info(UPDATING_COUPON, couponId);
                            ps.setLong(1, couponId);
                        }

                        @Override
                        public int getBatchSize() {
                            return coupons.size();
                        }
                    }
            );

            Map<Long, Integer> unassignedCouponCountByGroup = new HashMap<>();
            for (Coupon coupon : coupons) {
                classifyCouponUserUnassigned(coupon, unassignedCouponCountByGroup);
            }

            unassignedCouponCountByGroup.forEach((couponGroupId, count) -> {
                CouponGroup couponGroup = couponGroupRepository.findById(couponGroupId).orElse(null);
                if (couponGroup != null) {
                    couponGroup.reduceRemainingQuantity(count);
                    couponGroupRepository.save(couponGroup);
                }
            });
        };
    }

    private void classifyCouponUserUnassigned(Coupon coupon, Map<Long, Integer> unassignedCouponCountByGroup) {
        Long couponGroupId = coupon.getCouponGroup().getId();

        if (coupon.getUser() == null) {
            unassignedCouponCountByGroup.merge(couponGroupId, 1, Integer::sum);
        }
    }
}
