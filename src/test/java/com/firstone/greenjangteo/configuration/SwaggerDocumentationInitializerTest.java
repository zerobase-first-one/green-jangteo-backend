package com.firstone.greenjangteo.configuration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.LazyInitializationExcludeFilter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import springfox.documentation.spring.web.plugins.DocumentationPluginsBootstrapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SwaggerDocumentationInitializerTest {
    private static final int WEB_SERVER_START_PHASE = Integer.MAX_VALUE - 1;

    @InjectMocks
    private SwaggerDocumentationInitializer swaggerDocumentationInitializer;

    @Mock
    private DocumentationPluginsBootstrapper documentationPluginsBootstrapper;

    @DisplayName("웹 서버가 포트를 열기 전 단계에서 실행된다.")
    @Test
    void runsBeforeWebServerStarts() {
        // when, then
        assertThat(swaggerDocumentationInitializer.getPhase()).isLessThan(WEB_SERVER_START_PHASE);
    }

    @DisplayName("문서 스캔 전에 ObjectMapper가 준비되도록 핸들러 어댑터는 지연 초기화에서 제외한다.")
    @Test
    void excludesHandlerAdapterFromLazyInitialization() {
        // given
        LazyInitializationExcludeFilter filter = SwaggerDocumentationInitializer.handlerAdapterEagerInitialization();
        RootBeanDefinition beanDefinition = new RootBeanDefinition();

        // when
        boolean adapterExcluded = filter.isExcluded("adapter", beanDefinition, RequestMappingHandlerAdapter.class);
        boolean otherExcluded = filter.isExcluded("other", beanDefinition, Object.class);

        // then
        assertThat(adapterExcluded).isTrue();
        assertThat(otherExcluded).isFalse();
    }

    @DisplayName("시작하면 API 문서 스캔을 시작하고 실행 상태가 된다.")
    @Test
    void startsDocumentationScan() {
        // when
        swaggerDocumentationInitializer.start();

        // then
        verify(documentationPluginsBootstrapper).start();
        assertThat(swaggerDocumentationInitializer.isRunning()).isTrue();
    }

    @DisplayName("중지하면 실행 상태가 해제된다.")
    @Test
    void stops() {
        // given
        swaggerDocumentationInitializer.start();

        // when
        swaggerDocumentationInitializer.stop();

        // then
        assertThat(swaggerDocumentationInitializer.isRunning()).isFalse();
    }
}
