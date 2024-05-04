package ru.make.account.core.arving.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.make.account.core.arving.exception.ProcessException;
import ru.make.account.core.arving.model.Ticket;
import ru.make.account.core.arving.repository.TicketRepository;
import ru.make.account.core.arving.security.SecurityHandler;
import ru.make.account.core.arving.web.dto.operation.OperationDto;
import ru.make.account.core.arving.web.dto.operation.TicketDto;
import ru.make.account.core.arving.web.mapper.TicketMapper;

import java.math.BigDecimal;

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

    private void checkAccess(Long ticketId) {
        accountService.checkAccessToAccount(ticketId);
    }

    private void checkAccess(TicketDto ticket) {
        checkAccess(ticket.getAccountId());
    }

    private TicketDto toDto(Ticket ticket) {
        var result = ticketMapper.toDto(ticket);
        return result;
    }
}
