package com.firstone.greenjangteo;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource("classpath:application-test.properties")
@ContextConfiguration(classes = GreenJangteoApplication.class)
class GreenJangteoApplicationTests {
}
