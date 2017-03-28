package com.ankitkhanal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.UUID;

/**
 * Created by akhanal on 12/21/16.
 */
@Configuration
public class CorrelationIdConfig {

    Logger logger = LoggerFactory.getLogger(CorrelationIdConfig.class);

    @Value("${serviceId}")
    private String serviceId;

    @Bean
    public ClientHttpRequestInterceptor interceptor() {
        return (httpRequest, bytes, clientHttpRequestExecution) -> {
            HttpHeaders headers = httpRequest.getHeaders();
            headers.add("clientId", serviceId);
            String correlationId = MDC.get("correlationId");
            if(StringUtils.isEmpty(correlationId)){
                throw new RuntimeException("correlationId missing.");
            }
            headers.add("correlationId",correlationId);
            return clientHttpRequestExecution.execute(httpRequest, bytes);
        };
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate template = new RestTemplate();
        template.setInterceptors(Collections.singletonList(interceptor()));
        return template;
    }

    @Bean
    public Filter correlationIdFilter () {
        return new AbstractRequestLoggingFilter() {
            @Override protected void beforeRequest(HttpServletRequest request, String message) {
                String clientId = (String) request.getHeader("clientId");
                String correlationId = (String) request.getHeader("correlationId");
                if(StringUtils.isEmpty(correlationId)){
                    correlationId = UUID.randomUUID().toString();
                }
                MDC.put("clientId", clientId);
                MDC.put("correlationId", correlationId);
                MDC.put("serviceId", serviceId);
                logger.info(message);
            }

            @Override protected void afterRequest(HttpServletRequest request, String message) {
                logger.info(message);
                MDC.clear();
            }
        };
    }
}
