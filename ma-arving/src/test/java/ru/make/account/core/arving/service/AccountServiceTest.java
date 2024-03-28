package ru.make.account.core.arving.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import ru.make.account.core.arving.config.TestContainerConfiguration;
import ru.make.account.core.arving.web.dto.account.AccountDto;
import ru.make.account.core.arving.web.dto.currency.CurrencyDto;

import java.math.BigDecimal;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestContainerConfiguration.class)
public class AccountServiceTest {
    @Autowired
    private AccountService accountService;

    @Test
    public void testCreateAccountSuccess() throws Exception {
        var response = accountService.createAccount(AccountDto.builder()
                .id(488L)
                .comment("Общий счёт")
                .startSum(BigDecimal.valueOf(84445.59))
                .currency(CurrencyDto.builder()
                        .id(9L)
                        .build())
                .build());

        var accounts = accountService.getAccounts();
    }
}
