package com.bankingapp.cardservice.client;

import com.bankingapp.cardservice.config.FeignClientConfig;
import com.bankingapp.cardservice.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client to communicate with user-service.
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
