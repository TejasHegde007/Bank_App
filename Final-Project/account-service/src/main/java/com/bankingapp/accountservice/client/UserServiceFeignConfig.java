package com.bankingapp.accountservice.client;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Ensures Authorization header from the incoming request
 * is forwarded to UserService when AccountService calls it.
 */
public class UserServiceFeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
            if (attrs instanceof ServletRequestAttributes servletAttrs) {
                String auth = servletAttrs.getRequest().getHeader("Authorization");
                if (auth != null && !auth.isBlank()) {
                    requestTemplate.header("Authorization", auth);
                }
            }
        };
    }
}
