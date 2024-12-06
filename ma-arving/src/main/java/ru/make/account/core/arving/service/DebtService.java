package ru.make.account.core.arving.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.make.account.core.arving.exception.ProcessException;
import ru.make.account.core.arving.model.TicketDirectionEnum;
import ru.make.account.core.arving.model.debt.Debt;
import ru.make.account.core.arving.model.debt.DebtOperation;
import ru.make.account.core.arving.repository.debt.DebtOperationRepository;
import ru.make.account.core.arving.repository.debt.DebtRepository;
import ru.make.account.core.arving.web.dto.category.CategoryDto;
import ru.make.account.core.arving.web.dto.debt.DebtDto;
import ru.make.account.core.arving.web.dto.debt.DebtOperationDto;
import ru.make.account.core.arving.web.dto.operation.OperationDto;
import ru.make.account.core.arving.web.dto.ticket.TicketDto;
import ru.make.account.core.arving.web.mapper.DebtMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DebtService {
    public static final String DEBT_CATEGORY_NAME = "Долг";

    private final DebtRepository debtRepository;
    private final DebtOperationRepository debtOperationRepository;
    private final DebtMapper debtMapper;

    private final TicketService ticketService;
    private final CategoryService categoryService;

    private final AccountService accountService;

    public DebtDto getDebt(Long debtId) {
        log.info("Получение долга [{}]", debtId);
        var debt = debtRepository.findById(debtId)
                .map(this::mapDebt)
                .orElseThrow(() -> new ProcessException("Долг не найден"));
        accountService.checkAccessToAccount(debt.getAccountId());
        return debt;
    }

    @Transactional
    public Long createDebt(DebtDto debt) {
        log.info("Создание долга");
        accountService.checkAccessToAccount(debt.getAccountId());

        var entityDebt = debtMapper.toEntity(debt);
        entityDebt.setIsActive(Boolean.TRUE);
        entityDebt.setCreateDate(Optional.ofNullable(debt.getCreateDate()).orElse(LocalDate.now()));
        entityDebt.setSumDebt(entityDebt.getSumDebt());
        entityDebt.setSumDebtCurrent(entityDebt.getSumDebt());
        entityDebt.setDeleteFlag(Boolean.FALSE);
        debtRepository.save(entityDebt);
        log.info("> долг [{}]", entityDebt.getId());

        var debtFirstTicketId = ticketService.saveTicket(TicketDto.builder()
                .accountId(debt.getAccountId())
                .ticketDirection(debt.getDebtorFlag()
                        ? TicketDirectionEnum.INCOME
                        : TicketDirectionEnum.EXPENDITURE)
                .date(Optional.ofNullable(debt.getCreateDate()).orElse(LocalDate.now()))
                .operations(List.of(OperationDto.builder()
                        .name("Долг: " + debt.getName())
                        .comment(debt.getDescription())
                        .stuffFlag(Boolean.TRUE)
                        .sum(debt.getSumDebt())
                        .category(CategoryDto.builder()
                                .id(getDebtCategoryId(debt.getAccountId()))
                                .build()
                        )
                        .build()))
                .build());
        log.info("> чек по долгу [{}]", debtFirstTicketId);

        DebtOperation entityDebtOperation = new DebtOperation();
        entityDebtOperation.setDebtId(entityDebt.getId());
        entityDebtOperation.setDeleteFlag(Boolean.FALSE);
        entityDebtOperation.setFirstOperation(Boolean.TRUE);
        entityDebtOperation.setTicketId(debtFirstTicketId);
        debtOperationRepository.save(entityDebtOperation);
        log.info("> первая операция по долгу [{}]", entityDebtOperation.getId());
        return entityDebt.getId();
    }

    @Transactional
    public void addDebtOperation(DebtOperationDto debtOperation) {
        log.info("Добавление операции по долгу [{}]", debtOperation.getDebtId());
        var debt = getDebtById(debtOperation.getDebtId());
        accountService.checkAccessToAccount(debt.getAccountId());

        var debtTicket = Optional.ofNullable(debtOperation.getTicket())
                .orElseThrow(() -> new ProcessException("Отсутствует операция по долгу"));
        var firstOperation = debtOperationRepository
                .findDebtOperationByDebtIdAndFirstOperationIsTrue(debtOperation.getDebtId())
                .map(DebtOperation::getTicketId)
                .map(ticketService::getTicket)
                .orElseThrow(() -> new ProcessException("Отсутствует первая операция по долгу"));
        var direction = debt.getDebtorFlag()
                ? TicketDirectionEnum.EXPENDITURE
                : TicketDirectionEnum.INCOME;

        if (!debt.getIsActive() || debt.getDeleteFlag())
            throw new ProcessException("Выполнение операции невозможно");
        if (debtTicket.getDate().compareTo(firstOperation.getDate()) < 0)
            throw new ProcessException("Дата операции раньше создания долга");
        if (direction.equals(firstOperation.getTicketDirection()))
            throw new ProcessException("Некорректное направление операции долга");

        // создание чека операции долга
        var debtFirstTicketId = ticketService.saveTicket(TicketDto.builder()
                .accountId(debt.getAccountId())
                .ticketDirection(direction)
                .date(debtTicket.getDate())
                .operations(List.of(OperationDto.builder()
                        .name("Долг (операция): " + debt.getName())
                        .stuffFlag(Boolean.TRUE)
                        .sum(debtTicket.getTotalSum())
                        .category(CategoryDto.builder()
                                .id(getDebtCategoryId(debt.getAccountId()))
                                .build()
                        )
                        .build()))
                .build());
        log.info("> чек по долгу [{}]", debtFirstTicketId);

        // создание операции долга
        DebtOperation entityDebtOperation = new DebtOperation();
        entityDebtOperation.setDebtId(debt.getId());
        entityDebtOperation.setDeleteFlag(Boolean.FALSE);
        entityDebtOperation.setFirstOperation(Boolean.FALSE);
        entityDebtOperation.setTicketId(debtFirstTicketId);
        var responseDebtOperation = debtOperationRepository.save(entityDebtOperation);
        log.info("> операция по долгу [{}]", responseDebtOperation.getId());

        // корректировка состояния долга
        var newCurrentSum = calculateAmount(TicketDirectionEnum.EXPENDITURE,
                debt.getSumDebtCurrent(),
                debtTicket.getTotalSum());
        log.info("> текущая сумма долга [{}]", newCurrentSum);
        var changeDebt = getDebtById(debt.getId());
        changeDebt.setSumDebtCurrent(newCurrentSum);
        if (newCurrentSum.compareTo(BigDecimal.ZERO) <= 0) {
            changeDebt.setSumDebtCurrent(BigDecimal.ZERO);
            changeDebt.setIsActive(Boolean.FALSE);
            changeDebt.setCloseDate(debtTicket.getDate());
            log.info("> долг [{}] закрыт", debt.getId());
        }
        debtRepository.save(changeDebt);
        log.info("> состояние долга [{}] скорректировано", debt.getId());
    }

    @Transactional
    public void deleteDebt(Long debtId) {
        log.info("Удаление долга [{}]", debtId);
        var debt = getDebtById(debtId);
        accountService.checkAccessToAccount(debt.getAccountId());

        debtOperationRepository.findDebtOperationByDeleteFlagIsFalseAndDebtIdOrderByIdAsc(debtId)
                .forEach(debtOperation -> {
                    ticketService.removeTicket(debtOperation.getTicketId());
                    debtOperation.setDeleteFlag(Boolean.TRUE);
                    debtOperationRepository.save(debtOperation);
                });
        log.info("> операции долга [{}] удалены", debtId);

        debt.setDeleteFlag(Boolean.TRUE);
        debt.setIsActive(Boolean.FALSE);
        debtRepository.save(debt);
        log.info("> долг [{}] удален", debtId);
    }

    @Transactional
    public void deleteDebtOperation(Long debtOperationId) {
        log.info("Удаление операции долга [{}]", debtOperationId);
        var debtOperation = debtOperationRepository.findById(debtOperationId)
                .orElseThrow(() -> new ProcessException("Операция долга [" + debtOperationId + "] не найдена"));
        var debt = getDebtById(debtOperation.getDebtId());
        accountService.checkAccessToAccount(debt.getAccountId());

        var ticket = ticketService.getTicket(debtOperation.getTicketId());
        var operation = ticket.getOperations().get(0);

        ticketService.removeTicket(ticket.getId());
        log.info("> удалён чек [{}] по операции", debtOperation.getTicketId());
        debtOperation.setDeleteFlag(Boolean.TRUE);
        debtOperationRepository.save(debtOperation);
        log.info("> удалёна операция долга [{}]", debtOperationId);

        // корректировка долга
        var newCurrentSum = calculateAmount(TicketDirectionEnum.INCOME,
                debt.getSumDebtCurrent(),
                operation.getSum());
        debt.setSumDebtCurrent(newCurrentSum);
        debtRepository.save(debt);
        log.info("> текущая сумма долга [{}] скорректирована", debt.getId());
    }

    @Transactional
    public void closeDebt(DebtDto debt) {
        log.info("Закрытие долга [{}]", debt.getId());
        var debtEntity = getDebtById(debt.getId());
        accountService.checkAccessToAccount(debtEntity.getAccountId());

        debtEntity.setIsActive(Boolean.FALSE);
        LocalDate closeDate;
        if (Objects.isNull(debt.getCloseDate()))
            closeDate = LocalDate.now();
        else closeDate = debt.getCloseDate();
        debtEntity.setCloseDate(closeDate);
        debtRepository.save(debtEntity);
        log.info("> долг [{}] закрыт датой [{}]", debt.getId(), closeDate);
    }

    public List<DebtDto> getActiveDebts(Long accountId) {
        log.info("Получение списка активных долгов");
        accountService.checkAccessToAccount(accountId);
        var debts = debtRepository.getDebtByIsActiveIsTrueAndAccountIdOrderByCreateDateDesc(accountId);
        return debts.stream()
                .map(this::mapDebt)
                .collect(Collectors.toList());
    }

    private DebtDto mapDebt(Debt debt) {
        var debtDto = debtMapper.toDto(debt);
        debtDto.setDebtOperations(
                debtOperationRepository.findDebtOperationByDeleteFlagIsFalseAndDebtIdOrderByIdAsc(debt.getId()).stream()
                        .map(this::mapDebtOperationDto)
                        .collect(Collectors.toList())
        );

        return debtDto;
    }

    private DebtOperationDto mapDebtOperationDto(DebtOperation debtOperation) {
        var debtOperationDto = debtMapper.toDto(debtOperation);
        TicketDto ticket = ticketService.getTicket(debtOperation.getTicketId());
        var operation = ticket.getOperations().getFirst();
        debtOperationDto.setTicket(ticket);
        debtOperationDto.setSumOperation(operation.getSum());
        return debtOperationDto;
    }

    private Long getDebtCategoryId(Long accountId) {
        var category = categoryService.getOneCategoryByName(accountId, DEBT_CATEGORY_NAME);
        if (Objects.nonNull(category))
            return category.getId();
        else return categoryService.saveCategory(CategoryDto.builder()
                .relevant(Boolean.FALSE)
                .accountId(accountId)
                .name(DEBT_CATEGORY_NAME)
                .stuffFlag(Boolean.TRUE)
                .build());
    }

    private Debt getDebtById(Long debtId) {
        return debtRepository.findById(debtId)
                .orElseThrow(() -> new ProcessException("Долг [" + debtId + "] не найден"));
    }

    private BigDecimal calculateAmount(TicketDirectionEnum direction,
                                       BigDecimal baseAmount,
                                       BigDecimal value) {
        BigDecimal result;
        switch (direction) {
            case INCOME -> result = baseAmount.add(value);
            case EXPENDITURE -> result = baseAmount.subtract(value);
            default -> throw new ProcessException("Не задано направление операции");
        }
        return result;
    }
}
