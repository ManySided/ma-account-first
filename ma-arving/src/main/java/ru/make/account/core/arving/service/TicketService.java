package ru.make.account.core.arving.service;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.make.account.core.arving.exception.ProcessException;
import ru.make.account.core.arving.model.Ticket;
import ru.make.account.core.arving.model.TicketDirectionEnum;
import ru.make.account.core.arving.model.Ticket_;
import ru.make.account.core.arving.repository.TicketRepository;
import ru.make.account.core.arving.security.SecurityHandler;
import ru.make.account.core.arving.web.dto.operation.OperationDto;
import ru.make.account.core.arving.web.dto.ticket.TicketDto;
import ru.make.account.core.arving.web.dto.ticket.TicketFilterDto;
import ru.make.account.core.arving.web.dto.ticket.TicketListResponseDto;
import ru.make.account.core.arving.web.dto.ticket.TicketsOfDayDto;
import ru.make.account.core.arving.web.mapper.TicketMapper;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {
    private final TicketMapper ticketMapper;

    private final TicketRepository ticketRepository;

    private final OperationService operationService;
    private final AccountService accountService;

    private final SecurityHandler securityHandler;

    @Transactional
    public Long saveTicket(TicketDto request) {
        log.info("создание чека");
        checkAccess(request);
        var saveTicketDto = ticketMapper.toEntity(request);
        saveTicketDto.setUser(securityHandler.getUserId());
        var ticket = ticketRepository.save(saveTicketDto);
        log.info("создан чек [{}]", ticket.getId());

        var ticketSum = BigDecimal.ZERO;
        var operations = request.getOperations();
        log.info("создание операций в количестве [{}]", operations.size());
        for (OperationDto operation : operations) {
            ticketSum = ticketSum.add(operation.getSum());
            operation.setTicketId(ticket.getId());
            if (CollectionUtils.isNotEmpty(operation.getTags()))
                operation.getTags().forEach(tag -> tag.setAccountId(request.getAccountId()));
            operationService.saveOperation(operation);
        }

        changeCurrentSum(request.getAccountId(), request.getTicketDirection(), ticketSum);
        return ticket.getId();
    }

    @Transactional
    public Long updateTicket(TicketDto request) {
        log.info("обновление чека [{}]", request.getId());
        checkAccess(request.getAccountId());

        var ticket = ticketRepository.findById(request.getId())
                .orElseThrow(() -> new ProcessException("Чек не найден"));
        if (!ticket.getDate().equals(request.getDate())) {
            log.info("обновление даты чека [{}]", request.getId());
            ticket.setDate(request.getDate());
            ticketRepository.save(ticket);
        }
        if (!ticket.getTicketDirection().equals(request.getTicketDirection())) {
            var operations = operationService.getOperationByTicketId(ticket.getId());
            var ticketSum = getOperationSum(operations);
            log.info("откат суммы чека [{}]", request.getId());
            changeCurrentSum(ticket.getAccountId(),
                    reversDirection(ticket.getTicketDirection()),
                    ticketSum);
            log.info("добавление новой суммы чека [{}]", request.getId());
            changeCurrentSum(ticket.getAccountId(),
                    request.getTicketDirection(),
                    ticketSum);
            log.info("обновление направления с [{}] на [{}] для чека [{}]",
                    ticket.getTicketDirection(), request.getTicketDirection(), ticket.getId());
            ticket.setTicketDirection(request.getTicketDirection());
        }
        return ticket.getId();
    }

    @Transactional
    public Long updateOperationOfTicket(OperationDto request) {
        log.info("обновление операции [{}] в чеке", request.getId());
        var operation = operationService.getOperationById(request.getId());
        var ticketId = operation.getTicketId();
        var ticket = getTicket(ticketId);
        checkAccess(ticket.getAccountId());

        operationService.deleteOperation(request.getId());
        log.info("старая операция [{}] удалена", request.getId());
        log.info("откат суммы по операции [{}]", request.getId());
        changeCurrentSum(ticket.getAccountId(),
                reversDirection(ticket.getTicketDirection()),
                operation.getSum());

        log.info("создание новой операции");
        operation.setId(null);
        operation.setSum(request.getSum());
        operation.setName(request.getName());
        operation.setComment(request.getComment());
        operation.setCategory(request.getCategory());
        operation.setPurchaseId(request.getPurchaseId());
        operation.setTicketId(request.getTicketId());
        operation.setIsActive(Boolean.TRUE);
        var createdOperationId = operationService.saveOperation(operation);
        log.info("добавление суммы по новой операции операции [{}]", createdOperationId);
        changeCurrentSum(ticket.getAccountId(),
                ticket.getTicketDirection(),
                request.getSum());

        return createdOperationId;
    }

    @Transactional
    public void removeTicket(Long ticketId) {
        log.info("удаление чека [{}]", ticketId);
        var ticket = getTicket(ticketId);
        checkAccess(ticket.getAccountId());

        var operations = Optional.ofNullable(ticket.getOperations())
                .orElse(new ArrayList<>());
        if (operations.isEmpty())
            log.info("в чеке [{}] отсутствуют операции!", ticketId);
        else {
            operations.stream()
                    .map(OperationDto::getId)
                    .forEach(operationService::deleteOperation);

            var ticketSum = operations.stream()
                    .map(OperationDto::getSum)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            changeCurrentSum(ticket.getAccountId(),
                    reversDirection(ticket.getTicketDirection()),
                    ticketSum);
        }
    }

    @Transactional
    public void removeOperationOfTicket(Long operationId) {
        log.info("удаление операции [{}] чека", operationId);
        var operation = operationService.getOperationById(operationId);
        var ticketId = operation.getTicketId();
        var ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new ProcessException("Чек не найден"));
        checkAccess(ticket.getAccountId());

        var ticketDirection = ticket.getTicketDirection();
        var operationSum = operation.getSum();
        changeCurrentSum(ticket.getAccountId(),
                reversDirection(ticketDirection),
                operationSum);

        operationService.deleteOperation(operationId);
    }

    private void changeCurrentSum(Long accountId,
                                  TicketDirectionEnum direction,
                                  BigDecimal sum) {
        log.info("обновление суммы счёта");
        BigDecimal finalAmount;
        switch (direction) {
            // прибыль
            case INCOME -> finalAmount = sum;
            // убыток
            case EXPENDITURE -> finalAmount = sum.negate();
            default -> throw new ProcessException("Невозможно определить направление чека");
        }
        accountService.addSum(accountId, finalAmount);
    }

    private BigDecimal getOperationSum(List<OperationDto> operations) {
        return operations.stream()
                .map(OperationDto::getSum)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private TicketDirectionEnum reversDirection(TicketDirectionEnum direction) {
        TicketDirectionEnum resultDirection;
        switch (direction) {
            // прибыль
            case INCOME -> resultDirection = TicketDirectionEnum.EXPENDITURE;
            // убыток
            case EXPENDITURE -> resultDirection = TicketDirectionEnum.INCOME;
            default -> throw new ProcessException("Невозможно определить направление чека");
        }
        return resultDirection;
    }

    public TicketDto getTicket(Long request) {
        log.info("получение чека [{}]", request);
        var ticket = ticketRepository.findById(request)
                .orElseThrow(() -> new ProcessException("Чек не найден"));
        checkAccess(ticket.getAccountId());

        var result = ticketMapper.toDto(ticket);
        result.setOperations(operationService.getOperationByTicketId(request));

        return result;
    }

    public List<TicketDto> getTickets(TicketFilterDto request) {
        log.info("запрос на получение списка чеков");
        checkAccess(request.getAccountId());

        Specification<Ticket> spec = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.greaterThanOrEqualTo(
                    root.get(Ticket_.date),
                    Optional.ofNullable(request.getFrom()).orElseGet(() -> YearMonth.now().atDay(1)))
            );
            predicates.add(builder.lessThanOrEqualTo(
                    root.get(Ticket_.date),
                    Optional.ofNullable(request.getTo()).orElseGet(() -> YearMonth.now().atEndOfMonth()))
            );
            predicates.add(builder.equal(root.get(Ticket_.ACCOUNT_ID), request.getAccountId()));
            if (Objects.nonNull(request.getDirections())) {
                predicates.add(root.get(Ticket_.ticketDirection).in(request.getDirections()));
            }

            return builder.and(predicates.toArray(new Predicate[]{}));
        };

        var result = ticketRepository.findAll(spec, Sort.by(Sort.Order.desc(Ticket_.DATE))).stream()
                .map(ticket -> {
                    if (StringUtils.hasLength(request.getName()))
                        return toDto(ticket, request.getName());
                    return toDto(ticket);
                })
                .collect(Collectors.toList());
        log.info("найдено записей [{}]", result.size());
        return result;
    }

    public TicketListResponseDto getTicketsGroupByDay(TicketFilterDto request) {
        TicketListResponseDto result = new TicketListResponseDto();
        result.setDays(new ArrayList<>());

        for (TicketDto ticket : getTickets(request)) {
            if (CollectionUtils.isEmpty(ticket.getOperations()))
                continue;

            if (result.getDays().isEmpty()) {
                result.getDays().add(TicketsOfDayDto.builder()
                        .dayDate(ticket.getDate())
                        .sumOfDay(BigDecimal.ZERO)
                        .build());
            }
            var current = result.getDays().stream()
                    .reduce((first, second) -> second)
                    .get();

            var ticketTotalSum = ticket.getOperations().stream()
                    .map(OperationDto::getSum)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            ticket.setTotalSum(ticketTotalSum);

            var ticketDirection = ticket.getTicketDirection();
            if (current.getDayDate().equals(ticket.getDate())) {
                // дата одинаковая
                current.getTickets().add(ticket);
                switch (ticketDirection) {
                    case EXPENDITURE -> current.setSumOfDay(current.getSumOfDay().subtract(ticketTotalSum));
                    case INCOME -> current.setSumOfDay(current.getSumOfDay().add(ticketTotalSum));
                }
            } else {
                // дата отличается
                List<TicketDto> newTickets = new ArrayList<>();
                newTickets.add(ticket);
                var newDay = TicketsOfDayDto.builder()
                        .dayDate(ticket.getDate())
                        .tickets(newTickets)
                        .build();
                switch (ticketDirection) {
                    case EXPENDITURE -> newDay.setSumOfDay(BigDecimal.ZERO.subtract(ticketTotalSum));
                    case INCOME -> newDay.setSumOfDay(BigDecimal.ZERO.add(ticketTotalSum));
                }
                result.getDays().add(newDay);
            }
        }
        return result;
    }

    private void checkAccess(Long accountId) {
        accountService.checkAccessToAccount(accountId);
    }

    private void checkAccess(TicketDto ticket) {
        checkAccess(ticket.getAccountId());
    }

    private TicketDto toDto(Ticket ticket) {
        var result = ticketMapper.toDto(ticket);
        result.setOperations(operationService.getOperationByTicketId(ticket.getId()));
        return result;
    }

    private TicketDto toDto(Ticket ticket, String likeText) {
        var result = ticketMapper.toDto(ticket);
        result.setOperations(operationService.getOperationByTicketIdAndLikeName(ticket.getId(), likeText));
        return result;
    }
}
