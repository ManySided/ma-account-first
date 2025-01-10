package ru.make.account.core.arving.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import ru.make.account.core.arving.repository.OperationTagRepository;
import ru.make.account.core.arving.web.dto.operation.OperationTagDto;
import ru.make.account.core.arving.web.mapper.OperationTagMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OperationTagService {
    private final AccountService accountService;

    private final OperationTagMapper mapper;
    private final OperationTagRepository repository;

    public OperationTagDto saveTag(OperationTagDto tag) {
        log.info("создание тега операции");
        tag.setName(tag.getName().toLowerCase());
        checkAccess(tag.getAccountId());

        var tags = repository.findAllByNameAndAccountId(tag.getName(), tag.getAccountId());
        if (CollectionUtils.isNotEmpty(tags)) {
            var result = mapper.toDto(tags.get(0));
            log.info("тег {} уже создан ранее {}", result.getName(), result.getId());
        }

        var result = repository.save(mapper.toEntity(tag));
        log.info("создан тег {}", result.getId());
        return mapper.toDto(result);
    }

    public List<OperationTagDto> getTags(Long accountId) {
        log.info("получение тегов по счёту {}", accountId);
        checkAccess(accountId);

        return repository.findAllByAccountId(accountId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    private void checkAccess(Long accountId) {
        accountService.checkAccessToAccount(accountId);
    }

}
