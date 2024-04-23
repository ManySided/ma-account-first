package ru.make.account.core.arving.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.make.account.core.arving.exception.ProcessException;
import ru.make.account.core.arving.repository.AccountRepository;
import ru.make.account.core.arving.security.SecurityHandler;
import ru.make.account.core.arving.web.dto.account.AccountDto;
import ru.make.account.core.arving.web.mapper.AccountMapper;

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
        checkAccessToAccount(request.getId());
        log.info("> проверка безопасности пройдена");
        // TODO можно ли изменять текущую сумму, если уже есть операции
        var updateAccount = accountMapper.toEntity(request);
        var response = accountRepository.save(updateAccount);
        log.info("обновлён счёт [{}]", response.getId());
        return response.getId();
    }

    @Transactional
    public Long deleteAccount(Long request) {
        log.info("удаление счёта [{}]", request);
        checkAccessToAccount(request);
        log.info("> проверка безопасности пройдена");
        var deleteAccount = accountRepository.getReferenceById(request);
        deleteAccount.setActual(Boolean.FALSE);
        var response = accountRepository.save(deleteAccount);
        log.info("счёт [{}] деактивирован", response.getId());
        return response.getId();
    }

    public List<AccountDto> getAccounts() {
        log.info("получение активных счетов");
        return accountRepository.findAllByAccountCreatorOrderByCreatedDesc(securityHandler.getUserId()).stream()
                .map(item -> {
                    var dto = accountMapper.toDto(item);
                    dto.setCurrency(currencyService.getCurrency(item.getCurrencyId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private void checkAccessToAccount(Long accountId) {
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
