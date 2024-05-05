package ru.make.account.core.arving.service;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.make.account.core.arving.exception.ProcessException;
import ru.make.account.core.arving.model.Ticket;
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
import java.util.*;
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
            operationService.saveOperation(operation);
        }

        log.info("обновление суммы счёта");
        BigDecimal finalAmount;
        switch (request.getTicketDirection()) {
            // прибыль
            case INCOME -> finalAmount = ticketSum;
            // убыток
            case EXPENDITURE -> finalAmount = ticketSum.negate();
            default -> throw new ProcessException("Невозможно определить направление чека");
        }
        accountService.addSum(request.getAccountId(), finalAmount);

        return ticket.getId();
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

        List<Predicate> predicates = new ArrayList<>();
        Specification<Ticket> spec = (root, query, builder) -> {
            predicates.add(builder.greaterThanOrEqualTo(
                    root.get(Ticket_.date),
                    Optional.ofNullable(request.getFrom()).orElseGet(() -> YearMonth.now().atDay(1)))
            );
            predicates.add(builder.lessThanOrEqualTo(
                    root.get(Ticket_.date),
                    Optional.ofNullable(request.getFrom()).orElseGet(() -> YearMonth.now().atEndOfMonth()))
            );
            predicates.add(builder.equal(root.get(Ticket_.ACCOUNT_ID), request.getAccountId()));
            if (Objects.nonNull(request.getDirections())) {
                predicates.add(root.get(Ticket_.ticketDirection).in(request.getDirections()));
            }

            return builder.and(predicates.toArray(new Predicate[]{}));
        };

        var result = ticketRepository.findAll(spec, Sort.by(Sort.Order.desc(Ticket_.DATE))).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        log.info("найдено записей [{}]", result.size());
        return result;

    }

    public TicketListResponseDto getTicketsGroupByDay(TicketFilterDto request) {
        TicketListResponseDto result = new TicketListResponseDto();
        result.setDays(new ArrayList<>());

        for (TicketDto ticket : getTickets(request)) {
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

            if (current.getDayDate().equals(ticket.getDate())) {
                // дата одинаковая
                current.getTickets().add(ticket);
                current.setSumOfDay(current.getSumOfDay().add(ticketTotalSum));
            } else {
                // дата отличается
                result.getDays().add(TicketsOfDayDto.builder()
                        .dayDate(ticket.getDate())
                        .sumOfDay(ticketTotalSum)
                        .tickets(Collections.singletonList(ticket))
                        .build());
            }
        }
        return result;
    }

    private void checkAccess(Long ticketId) {
        accountService.checkAccessToAccount(ticketId);
    }

    private void checkAccess(TicketDto ticket) {
        checkAccess(ticket.getAccountId());
    }

    private TicketDto toDto(Ticket ticket) {
        var result = ticketMapper.toDto(ticket);
        result.setOperations(operationService.getOperationByTicketId(ticket.getId()));
        return result;
    }
}
