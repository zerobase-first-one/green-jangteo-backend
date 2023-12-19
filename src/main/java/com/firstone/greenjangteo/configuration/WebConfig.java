package com.firstone.greenjangteo.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${spring.frontend.url}")
    private String frontendUrl;

    @Value("${spring.frontend.local.url}")
    private String frontendLocalUrl;

    @Value("${spring.frontend.local.port}")
    private String frontendLocalPort;

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**")
                .allowedOrigins(
                        frontendUrl,
                        frontendLocalUrl + ":" + frontendLocalPort
                )
                .allowedMethods(
                        HttpMethod.GET.name(),
                        HttpMethod.HEAD.name(),
                        HttpMethod.POST.name(),
                        HttpMethod.PUT.name(),
                        HttpMethod.PATCH.name(),
                        HttpMethod.DELETE.name()
                )
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
