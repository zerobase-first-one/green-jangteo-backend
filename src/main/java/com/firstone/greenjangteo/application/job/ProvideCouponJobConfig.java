package com.firstone.greenjangteo.application.job;

import com.firstone.greenjangteo.coupon.model.entity.Coupon;
import com.firstone.greenjangteo.coupon.model.entity.CouponGroup;
import com.firstone.greenjangteo.coupon.repository.CouponGroupRepository;
import com.firstone.greenjangteo.user.model.entity.User;
import com.firstone.greenjangteo.user.repository.UserRepository;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.firstone.greenjangteo.application.job.utility.LogConstant.UPDATING_COUPON;
import static com.firstone.greenjangteo.application.job.utility.LogConstant.UPDATING_USER;

@Configuration
@RequiredArgsConstructor
public class ProvideCouponJobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final CouponGroupRepository couponGroupRepository;
    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;

    private static final Logger log = LoggerFactory.getLogger(ProvideCouponJobConfig.class);

    private static final String JOB_NAME = "provideCouponJob";
    private static final String STEP_NAME = "provideCouponStep";
    private static final String UPDATE_USER_QUERY = "UPDATE coupon SET user_id = ? WHERE id = ?";

    @Bean
    public Job provideCouponJob(Step provideCouponStep) {
        return jobBuilderFactory.get(JOB_NAME)
                .start(provideCouponStep)
                .build();
    }

    @Bean
    public Step provideCouponStep() {
        CouponGroupReader reader = new CouponGroupReader();
        return stepBuilderFactory.get(STEP_NAME)
                .<CouponGroup, List<Coupon>>chunk(100)
                .reader(reader)
                .processor(couponProcessor(reader))
                .writer(couponWriter())
                .build();
    }

    public class CouponGroupReader implements ItemReader<CouponGroup>, StepExecutionListener {
        private Long couponGroupId;
        private String parsedUserIds;
        private boolean read;

        @Override
        public void beforeStep(StepExecution stepExecution) {
            JobParameters jobParameters = stepExecution.getJobParameters();
            this.couponGroupId = jobParameters.getLong("couponGroupId");
            this.parsedUserIds = jobParameters.getString("userIds");
            this.read = false; // 각 Step 실행 전에 read를 false로 설정
        }

        @Override
        public CouponGroup read() {
            if (!read) {
                read = true;
                return couponGroupRepository.findById(couponGroupId).orElse(null);
            } else {
                return null;
            }
        }

        @Override
        public ExitStatus afterStep(StepExecution stepExecution) {
            return null;
        }

        public String getParsedUserIds() {
            return parsedUserIds;
        }
    }

    public ItemProcessor<CouponGroup, List<Coupon>> couponProcessor(CouponGroupReader reader) {
        return couponGroup -> {
            List<Long> userIds = Arrays.stream(reader.getParsedUserIds().split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            List<Coupon> couponsToUpdate = new ArrayList<>();
            List<Coupon> unassignedCoupons = couponGroup.getUnassignedCoupons();

            int couponsPerUser = unassignedCoupons.size() / userIds.size();

            for (Long userId : userIds) {
                User user = userRepository.findById(userId).orElse(null);
                if (user != null) {
                    List<Coupon> couponsForUser = unassignedCoupons.stream()
                            .limit(couponsPerUser)
                            .collect(Collectors.toList());
                    couponsToUpdate.addAll(addUserToCoupons(couponsForUser, user));

                    unassignedCoupons.removeAll(couponsForUser);
                }
            }

            couponGroup.reduceRemainingQuantity(couponsToUpdate.size());

            return couponsToUpdate;
        };
    }

    private List<Coupon> addUserToCoupons(List<Coupon> coupons, User user) {
        return coupons.stream()
                .filter(coupon -> coupon.getUser() == null)
                .peek(coupon -> coupon.addUser(user))
                .collect(Collectors.toList());
    }

    @Bean
    public ItemWriter<List<Coupon>> couponWriter() {
        return listOfCouponsLists -> {
            for (List<Coupon> coupons : listOfCouponsLists) {
                jdbcTemplate.batchUpdate(
                        UPDATE_USER_QUERY,
                        new BatchPreparedStatementSetter() {
                            @Override
                            public void setValues(PreparedStatement ps, int i) throws SQLException {
                                Coupon coupon = coupons.get(i);
                                Long couponId = coupon.getId();
                                Long userId = coupon.getUser().getId();

                                log.info(UPDATING_COUPON + UPDATING_USER, couponId, userId);

                                ps.setLong(1, userId);
                                ps.setLong(2, couponId);
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
