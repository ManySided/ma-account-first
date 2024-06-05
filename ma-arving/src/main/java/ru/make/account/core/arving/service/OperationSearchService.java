package ru.make.account.core.arving.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.make.account.core.arving.repository.OperationRepository;
import ru.make.account.core.arving.web.dto.operation.OperationDto;

import java.util.List;

import static ru.make.account.core.arving.util.SearchUtils.likeText;

@Service
@RequiredArgsConstructor
@Slf4j
public class OperationSearchService {
    private final AccountService accountService;

    private final OperationService operationService;

    private final OperationRepository operationRepository;

    public List<String> findGroupOperationsByName(String name, Long accountId) {
        accountService.checkAccessToAccount(accountId);
        return operationRepository.findOperationGroups(accountId, likeText(name));
    }

    public OperationDto findLastOperationByName(String name, Long accountId) {
        accountService.checkAccessToAccount(accountId);
        return operationRepository.findOperationsByName(accountId, name)
                .stream()
                .findFirst()
                .map(operationService::toDto)
                .orElse(null);
    }
}
