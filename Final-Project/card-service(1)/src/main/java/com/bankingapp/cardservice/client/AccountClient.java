package com.bankingapp.cardservice.client;

import com.bankingapp.cardservice.config.FeignClientConfig;
import com.bankingapp.cardservice.dto.AccountDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client to communicate with account-service.
 */
@FeignClient(
        name = "accountservice",
        url = "${services.accounts.url}",
        configuration = FeignClientConfig.class
)
public interface AccountClient {

    @GetMapping("/api/accounts/{id}")
    AccountDto getAccountById(@PathVariable("id") String id);
}
