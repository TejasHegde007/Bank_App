package com.bankingapp.accountservice.client;

import com.bankingapp.accountservice.dto.UserSummaryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client to communicate with UserService.
 * URL is injected via userservice.url property in application.yml
 */
@FeignClient(
        name = "user-service",
        url = "${userservice.url}",
        configuration = UserServiceFeignConfig.class
)
public interface UserServiceClient {

    @GetMapping("/api/users/{id}")
    UserSummaryDto getUserById(@PathVariable("id") Long id);
}
