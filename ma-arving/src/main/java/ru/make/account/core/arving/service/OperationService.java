package ru.make.account.core.arving.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.make.account.core.arving.model.Operation;
import ru.make.account.core.arving.repository.OperationRepository;
import ru.make.account.core.arving.web.dto.operation.OperationDto;
import ru.make.account.core.arving.web.mapper.OperationMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OperationService {
    private final OperationMapper operationMapper;

    private final OperationRepository operationRepository;

    private final CategoryService categoryService;

    public Long saveOperation(OperationDto request) {
        log.info("создание операции");
        var operationEntity = operationMapper.toEntity(request);
        operationEntity.setStuffFlag(Boolean.FALSE);
        operationEntity.setIsActive(Boolean.TRUE);

        operationEntity = operationRepository.save(operationEntity);
        log.info("создана операция [{}]", operationEntity.getId());

        return operationEntity.getId();
    }

    public List<OperationDto> getOperationByTicketId(Long ticketId) {
        var operations = operationRepository.findByTicketIdAndIsActiveTrueOrderByIdAsc(ticketId);
        return operations.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private OperationDto toDto(Operation operation) {
        var result = operationMapper.toDto(operation);
        result.setCategory(categoryService.getCategoryById(operation.getCategoryId()));
        return result;
    }
}
