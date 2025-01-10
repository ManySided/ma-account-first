package ru.make.account.core.arving.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.make.account.core.arving.exception.ProcessException;
import ru.make.account.core.arving.model.Operation;
import ru.make.account.core.arving.repository.OperationRepository;
import ru.make.account.core.arving.web.dto.operation.OperationDto;
import ru.make.account.core.arving.web.mapper.OperationMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.make.account.core.arving.util.SearchUtils.likeText;

@Service
@RequiredArgsConstructor
@Slf4j
public class OperationService {
    private final OperationMapper operationMapper;

    private final OperationRepository operationRepository;

    private final CategoryService categoryService;

    @Transactional
    public Long saveOperation(OperationDto request) {
        log.info("создание операции");
        var operationEntity = operationMapper.toEntity(request);

        operationEntity.setStuffFlag(Optional.of(request).map(OperationDto::getStuffFlag).orElse(Boolean.FALSE));
        operationEntity.setIsActive(Boolean.TRUE);

        operationEntity = operationRepository.save(operationEntity);
        log.info("создана операция [{}]", operationEntity.getId());

        return operationEntity.getId();
    }

    @Transactional
    public void deleteOperation(Long operationId) {
        log.info("удаление операции [{}]", operationId);
        var operation = operationRepository.findById(operationId)
                .orElseThrow(() -> new ProcessException("Операция не найдена"));

        operation.setIsActive(Boolean.FALSE);
        operationRepository.save(operation);
    }

    public OperationDto getOperationById(Long operationId) {
        var operation = operationRepository.findById(operationId)
                .orElseThrow(() -> new ProcessException("Операция не найдена"));
        return toDto(operation);
    }

    public List<OperationDto> getOperationByTicketId(Long ticketId) {
        var operations = operationRepository.findByTicketIdAndIsActiveTrueOrderByIdAsc(ticketId);
        return operations.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<OperationDto> getOperationByTicketIdAndLikeName(Long ticketId, String name) {
        var operations = operationRepository.findLikeText(ticketId, likeText(name));
        return operations.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public OperationDto toDto(Operation operation) {
        var result = operationMapper.toDto(operation);
        result.setCategory(categoryService.getCategoryById(operation.getCategoryId()));
        return result;
    }
}
