package ru.make.account.core.arving.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.make.account.core.arving.exception.ProcessException;
import ru.make.account.core.arving.model.Account;
import ru.make.account.core.arving.repository.AccountRepository;
import ru.make.account.core.arving.security.SecurityHandler;
import ru.make.account.core.arving.web.dto.account.AccountDto;
import ru.make.account.core.arving.web.mapper.AccountMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {
    private final SecurityHandler securityHandler;
    private final CurrencyService currencyService;

    private final AccountMapper accountMapper;
    private final AccountRepository accountRepository;

    @Transactional
    public Long createAccount(AccountDto request) {
        log.info("создание счёта");
        var createAccount = accountMapper.toEntity(request);
        createAccount.setId(null);
        createAccount.setAccountCreator(securityHandler.getUserId());
        createAccount.setActual(Boolean.TRUE);
        createAccount.setCurrentSum(request.getStartSum());
        createAccount.setCreated(LocalDateTime.now());
        var response = accountRepository.save(createAccount);
        log.info("создан счёта [{}]", response.getId());
        return response.getId();
    }

    @Transactional
    public Long updateAccount(AccountDto request) {
        log.info("обновление счёта [{}]", request.getId());
        var oldAccount = accountRepository.findById(request.getId())
                .orElseThrow(() -> new ProcessException("Счёт не найден"));
        checkAccessToAccount(request.getId());
        log.info("> проверка безопасности пройдена");
        // TODO можно ли изменять текущую сумму, если уже есть операции
        var updateAccount = accountMapper.toEntity(request);
        updateAccount.setAccountCreator(oldAccount.getAccountCreator());
        var response = accountRepository.save(updateAccount);
        log.info("обновлён счёт [{}]", response.getId());
        return response.getId();
    }

    @Transactional
    public Long deleteAccount(Long request) {
        log.info("удаление счёта [{}]", request);
        checkAccessToAccount(request);
        log.info("> проверка безопасности пройдена");
        var deleteAccount = accountRepository.findById(request)
                .orElseThrow(() -> new ProcessException("Счёт не найден"));
        deleteAccount.setActual(Boolean.FALSE);
        var response = accountRepository.save(deleteAccount);
        log.info("счёт [{}] деактивирован", response.getId());
        return response.getId();
    }

    public List<AccountDto> getAccounts() {
        log.info("получение активных счетов");
        return accountRepository.findAllByAccountCreatorOrderByCreatedDesc(securityHandler.getUserId()).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public AccountDto getAccount(Long request) {
        log.info("получение счета по ID [{}]", request);
        checkAccessToAccount(request);
        return accountRepository.findById(request)
                .map(this::toDto)
                .orElse(null);
    }

    @Transactional
    public void addSum(Long accountId, BigDecimal sum) {
        log.info("> обновление суммы счёта [{}]; добавление суммы  [{}]", accountId, sum);
        var account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ProcessException("Счёт не найден"));
        if (account.getAccountCreator().equals(securityHandler.getUserId())) {
            var currentSum = account.getCurrentSum();
            log.info("> текущая сумма [{}]", currentSum);
            var newSum = currentSum.add(sum);
            account.setCurrentSum(newSum);
            log.info("> текущая сумма счёта [{}] обновлена до суммы [{}]", accountId, newSum);
            accountRepository.save(account);
        } else throw new ProcessException("Счёт не принадлежит пользователю");
    }

    public AccountDto toDto(Account account) {
        var dto = accountMapper.toDto(account);
        dto.setCurrency(currencyService.getCurrency(account.getCurrencyId()));
        return dto;
    }

    public void checkAccessToAccount(Long accountId) {
        if (!canChangeAccount(accountId))
            throw new ProcessException("Данная операция недоступна");
    }

    private boolean canChangeAccount(Long accountId) {
        var userId = securityHandler.getUserId();
        var account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ProcessException("Счёт с идентификатором " + accountId + " не найден"));
        return account.getAccountCreator().equals(userId);
    }
}
