package com.bankingapp.loanservice.client;

import com.bankingapp.loanservice.config.FeignClientConfig;
import com.bankingapp.loanservice.dto.AccountDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for account-service.
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
