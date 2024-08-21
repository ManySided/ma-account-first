package ru.make.account.core.arving.service;

import org.junit.Before;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import ru.make.account.core.arving.model.Category;
import ru.make.account.core.arving.model.Currency;
import ru.make.account.core.arving.repository.CategoryRepository;
import ru.make.account.core.arving.repository.CurrencyRepository;
import ru.make.account.core.arving.security.SecurityHandler;
import ru.make.account.core.arving.web.dto.account.AccountDto;
import ru.make.account.core.arving.web.dto.currency.CurrencyDto;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AbstractTestBase {
    private final Map<String, Long> mapAccount2Id = new HashMap<>();
    private final Map<String, Long> mapCategory2Id = new HashMap<>();
    private final Map<String, Long> mapCurrency2Id = new HashMap<>();

    @MockBean
    protected JwtDecoder jwtDecoder;

    @MockBean
    protected SecurityHandler securityHandler;

    @Autowired
    private CurrencyRepository currencyRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private AccountService accountService;

    private final String CURRENCY_RUB = "rub";
    private final String ACCOUNT_TEST = "test account";
    private final String CATEGORY_PRODUCTS = "food";
    private final String CATEGORY_TRANSPORT = "transport";
    private final String CATEGORY_OTHER = "other";

    @Before
    public void setUp() {
        Mockito.when(securityHandler.getUserId())
                .thenReturn(UUID.fromString("46e707b0-1386-4311-9380-c67f6580dcff"));
    }

    public Long getCurrencyRubId() {
        return mapCurrency2Id.computeIfAbsent(CURRENCY_RUB, s -> {
            Currency currency = new Currency();
            currency.setName(CURRENCY_RUB);
            currency.setShortName(CURRENCY_RUB);
            return currencyRepository.save(currency).getId();
        });
    }

    public Long getAccountId() {
        return mapAccount2Id.computeIfAbsent(ACCOUNT_TEST, s ->
                accountService.createAccount(AccountDto.builder()
                        .name(ACCOUNT_TEST)
                        .comment(ACCOUNT_TEST + " comment")
                        .currency(CurrencyDto.builder()
                                .id(getCurrencyRubId())
                                .build())
                        .startSum(BigDecimal.valueOf(10000))
                        .build())
        );
    }

    public Long getAccountId(final String accountName) {
        return mapAccount2Id.computeIfAbsent(accountName, s ->
                accountService.createAccount(AccountDto.builder()
                        .name(accountName)
                        .comment(accountName + " comment")
                        .currency(CurrencyDto.builder()
                                .id(getCurrencyRubId())
                                .build())
                        .startSum(BigDecimal.valueOf(10000))
                        .build())
        );
    }

    public AccountDto getAccount() {
        return accountService.getAccount(getAccountId());
    }

    public Long getCategoryProductsId() {
        return mapCategory2Id.computeIfAbsent(CATEGORY_PRODUCTS, s -> {
                    Category category = new Category();
                    category.setAccountId(getAccountId());
                    category.setName(CATEGORY_PRODUCTS);
                    category.setStuffFlag(Boolean.FALSE);
                    category.setFlagActivity(Boolean.TRUE);
                    return categoryRepository.save(category).getId();
                }
        );
    }

    public Long getCategoryTransportId() {
        return mapCategory2Id.computeIfAbsent(CATEGORY_TRANSPORT, s -> {
                    Category category = new Category();
                    category.setAccountId(getAccountId());
                    category.setName(CATEGORY_TRANSPORT);
                    category.setStuffFlag(Boolean.FALSE);
                    category.setFlagActivity(Boolean.TRUE);
                    return categoryRepository.save(category).getId();
                }
        );
    }

    public Long getCategoryOtherId() {
        return mapCategory2Id.computeIfAbsent(CATEGORY_OTHER, s -> {
                    Category category = new Category();
                    category.setAccountId(getAccountId());
                    category.setName(CATEGORY_OTHER);
                    category.setStuffFlag(Boolean.FALSE);
                    category.setFlagActivity(Boolean.TRUE);
                    return categoryRepository.save(category).getId();
                }
        );
    }
}
