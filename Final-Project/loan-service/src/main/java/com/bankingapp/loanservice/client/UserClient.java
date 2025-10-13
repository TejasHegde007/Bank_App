package com.bankingapp.loanservice.client;

import com.bankingapp.loanservice.config.FeignClientConfig;
import com.bankingapp.loanservice.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for user-service.
 */
@FeignClient(
        name = "userservice",
        url = "${services.users.url}",
        configuration = FeignClientConfig.class
)
public interface UserClient {

    @GetMapping("/api/users/{id}")
    UserDto getUserById(@PathVariable("id") String id);
}
