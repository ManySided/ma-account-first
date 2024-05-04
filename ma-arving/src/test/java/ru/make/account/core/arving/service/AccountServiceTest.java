package ru.make.account.core.arving.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.make.account.core.arving.web.dto.account.AccountDto;
import ru.make.account.core.arving.web.dto.currency.CurrencyDto;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class AccountServiceTest extends AbstractTestBase {
    @Autowired
    private AccountService accountService;

    @Test
    public void testCreateAccountSuccess() throws Exception {
        var response = accountService.createAccount(AccountDto.builder()
                .name("test account")
                .comment("Общий счёт")
                .startSum(BigDecimal.valueOf(84445.59))
                .currency(CurrencyDto.builder()
                        .id(getCurrencyRubId())
                        .build())
                .build());

        var account = accountService.getAccount(response);
        assertEquals("test account", account.getName());
        assertEquals("Общий счёт", account.getComment());
        assertEquals(getCurrencyRubId(), account.getCurrency().getId());
        assertEquals(BigDecimal.valueOf(84445.59), account.getCurrentSum());
        assertEquals(BigDecimal.valueOf(84445.59), account.getStartSum());
        assertEquals(Boolean.TRUE, account.getActual());
    }
}
