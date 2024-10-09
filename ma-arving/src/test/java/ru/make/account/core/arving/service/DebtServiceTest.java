package ru.make.account.core.arving.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.make.account.core.arving.exception.ProcessException;
import ru.make.account.core.arving.model.TicketDirectionEnum;
import ru.make.account.core.arving.web.dto.debt.DebtDto;
import ru.make.account.core.arving.web.dto.debt.DebtOperationDto;
import ru.make.account.core.arving.web.dto.ticket.TicketDto;
import ru.make.account.core.arving.web.dto.ticket.TicketFilterDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class DebtServiceTest extends AbstractTestBase {
    @Autowired
    DebtService debtService;

    @Autowired
    AccountService accountService;

    @Autowired
    TicketService ticketService;

    @Test
    public void testCreateAddDeleteDebtSuccess() throws Exception {
        var debtDate = LocalDate.of(2024, 10, 10);

        // создаём долг
        var accountId = getAccountId("test_create_update_debt");
        var debtId = debtService.createDebt(DebtDto.builder()
                .accountId(accountId)
                .name("Одолжил Ивану на стрилку")
                .description("Ему не хватало, а стирать очень надо")
                .sumDebt(BigDecimal.valueOf(8050.50))
                .createDate(debtDate)
                .debtorFlag(Boolean.FALSE)
                .build());

        // проверяем созданный долг
        var debt = debtService.getDebt(debtId);
        assertEquals(Boolean.TRUE, debt.getIsActive());
        assertEquals(debtDate, debt.getCreateDate());
        assertNull(debt.getCloseDate());
        assertEquals("Одолжил Ивану на стрилку", debt.getName());
        assertEquals("Ему не хватало, а стирать очень надо", debt.getDescription());
        assertEquals(0, BigDecimal.valueOf(8050.5).compareTo(debt.getSumDebt()));
        assertEquals(0, BigDecimal.valueOf(8050.5).compareTo(debt.getSumDebtCurrent()));
        assertEquals(accountId, debt.getAccountId());
        assertEquals(Boolean.FALSE, debt.getDebtorFlag());
        assertEquals(Boolean.FALSE, debt.getDeleteFlag());

        var debtOperations = debt.getDebtOperations();
        assertEquals(1, debtOperations.size());

        var debtOperation = debtOperations.get(0);
        assertEquals(Boolean.TRUE, debtOperation.getFirstOperation());
        assertEquals(Boolean.FALSE, debtOperation.getDeleteFlag());
        assertEquals(debt.getId(), debtOperation.getDebtId());

        var ticket = debtOperation.getTicket();
        assertNotNull(ticket.getId());
        assertEquals(TicketDirectionEnum.EXPENDITURE, ticket.getTicketDirection());
        assertEquals(debtDate, ticket.getDate());

        var operations = ticket.getOperations();
        assertEquals(1, operations.size());

        var operation = operations.get(0);
        assertEquals(0, BigDecimal.valueOf(8050.50).compareTo(operation.getSum()));
        assertEquals("Долг: Одолжил Ивану на стрилку", operation.getName());
        assertEquals("Ему не хватало, а стирать очень надо", operation.getComment());
        assertEquals("Долг", operation.getCategory().getName());
        assertEquals(Boolean.TRUE, operation.getIsActive());
        assertEquals(Boolean.TRUE, operation.getStuffFlag());

        var account = accountService.getAccount(accountId);
        assertEquals(0, BigDecimal.valueOf(1949.50).compareTo(account.getCurrentSum()));

        // добавляем операцию погашения долга
        var debtOperationDate = LocalDate.of(2024, 10, 11);
        debtService.addDebtOperation(DebtOperationDto.builder()
                .debtId(debtId)
                .ticket(TicketDto.builder()
                        .date(debtOperationDate)
                        .totalSum(BigDecimal.valueOf(4000))
                        .build())
                .build());
        // проверяем операции долга
        debt = debtService.getDebt(debtId);
        assertEquals(Boolean.TRUE, debt.getIsActive());
        assertEquals(debtDate, debt.getCreateDate());
        assertNull(debt.getCloseDate());
        assertEquals("Одолжил Ивану на стрилку", debt.getName());
        assertEquals("Ему не хватало, а стирать очень надо", debt.getDescription());
        assertEquals(0, BigDecimal.valueOf(8050.50).compareTo(debt.getSumDebt()));
        assertEquals(0, BigDecimal.valueOf(4050.50).compareTo(debt.getSumDebtCurrent()));
        assertEquals(accountId, debt.getAccountId());
        assertEquals(Boolean.FALSE, debt.getDebtorFlag());
        assertEquals(Boolean.FALSE, debt.getDeleteFlag());

        debtOperations = debt.getDebtOperations();
        assertEquals(2, debtOperations.size());

        debtOperation = debtOperations.stream()
                .filter(DebtOperationDto::getFirstOperation)
                .findFirst()
                .orElseThrow(() -> new ProcessException("Нет первой операции"));
        assertEquals(Boolean.TRUE, debtOperation.getFirstOperation());
        assertEquals(Boolean.FALSE, debtOperation.getDeleteFlag());
        assertEquals(debt.getId(), debtOperation.getDebtId());

        debtOperation = debtOperations.stream()
                .filter(item -> !item.getFirstOperation())
                .findFirst()
                .orElseThrow(() -> new ProcessException("Нет первой операции"));
        assertEquals(Boolean.FALSE, debtOperation.getFirstOperation());
        assertEquals(Boolean.FALSE, debtOperation.getDeleteFlag());
        assertEquals(debt.getId(), debtOperation.getDebtId());

        ticket = debtOperation.getTicket();
        assertNotNull(ticket.getId());
        assertEquals(TicketDirectionEnum.INCOME, ticket.getTicketDirection());
        assertEquals(debtOperationDate, ticket.getDate());

        operations = ticket.getOperations();
        assertEquals(1, operations.size());

        operation = operations.get(0);
        assertEquals(0, BigDecimal.valueOf(4000.00).compareTo(operation.getSum()));
        assertEquals("Долг (операция): Одолжил Ивану на стрилку", operation.getName());
        assertNull(operation.getComment());
        assertEquals("Долг", operation.getCategory().getName());
        assertEquals(Boolean.TRUE, operation.getIsActive());
        assertEquals(Boolean.TRUE, operation.getStuffFlag());

        account = accountService.getAccount(accountId);
        assertEquals(0, BigDecimal.valueOf(5949.50).compareTo(account.getCurrentSum()));

        var tickets = ticketService.getTickets(TicketFilterDto.builder()
                        .accountId(accountId)
                        .from(LocalDate.of(2024, 10, 1))
                        .to(LocalDate.of(2024, 10, 27))
                        .build())
                .stream().filter(item -> !item.getOperations().isEmpty()).collect(Collectors.toList());
        assertEquals(2, tickets.size());

        // удаляем операцию долга
        debtService.deleteDebtOperation(debtOperation.getId());
        // проверяем операции долга
        debt = debtService.getDebt(debtId);
        assertEquals(Boolean.TRUE, debt.getIsActive());
        assertEquals(debtDate, debt.getCreateDate());
        assertNull(debt.getCloseDate());
        assertEquals("Одолжил Ивану на стрилку", debt.getName());
        assertEquals("Ему не хватало, а стирать очень надо", debt.getDescription());
        assertEquals(0, BigDecimal.valueOf(8050.50).compareTo(debt.getSumDebt()));
        assertEquals(0, BigDecimal.valueOf(8050.50).compareTo(debt.getSumDebtCurrent()));
        assertEquals(accountId, debt.getAccountId());
        assertEquals(Boolean.FALSE, debt.getDebtorFlag());
        assertEquals(Boolean.FALSE, debt.getDeleteFlag());

        debtOperations = debt.getDebtOperations();
        assertEquals(1, debtOperations.size());

        debtOperation = debtOperations.get(0);
        assertEquals(Boolean.TRUE, debtOperation.getFirstOperation());
        assertEquals(Boolean.FALSE, debtOperation.getDeleteFlag());
        assertEquals(debt.getId(), debtOperation.getDebtId());

        ticket = debtOperation.getTicket();
        assertNotNull(ticket.getId());
        assertEquals(TicketDirectionEnum.EXPENDITURE, ticket.getTicketDirection());
        assertEquals(debtDate, ticket.getDate());

        operations = ticket.getOperations();
        assertEquals(1, operations.size());

        operation = operations.get(0);
        assertEquals(0, BigDecimal.valueOf(8050.50).compareTo(operation.getSum()));
        assertEquals("Долг: Одолжил Ивану на стрилку", operation.getName());
        assertEquals("Ему не хватало, а стирать очень надо", operation.getComment());
        assertEquals("Долг", operation.getCategory().getName());
        assertEquals(Boolean.TRUE, operation.getIsActive());
        assertEquals(Boolean.TRUE, operation.getStuffFlag());

        account = accountService.getAccount(accountId);
        assertEquals(0, BigDecimal.valueOf(1949.50).compareTo(account.getCurrentSum()));

        tickets = ticketService.getTickets(TicketFilterDto.builder()
                        .accountId(accountId)
                        .from(LocalDate.of(2024, 10, 1))
                        .to(LocalDate.of(2024, 10, 27))
                        .build())
                .stream().filter(item -> !item.getOperations().isEmpty()).collect(Collectors.toList());
        assertEquals(1, tickets.size());

        // удаляем долг
        debtService.deleteDebt(debtId);
        // проверяем удаление долга
        debt = debtService.getDebt(debtId);
        assertEquals(Boolean.FALSE, debt.getIsActive());
        assertEquals(debtDate, debt.getCreateDate());
        assertNull(debt.getCloseDate());
        assertEquals("Одолжил Ивану на стрилку", debt.getName());
        assertEquals("Ему не хватало, а стирать очень надо", debt.getDescription());
        assertEquals(0, BigDecimal.valueOf(8050.50).compareTo(debt.getSumDebt()));
        assertEquals(0, BigDecimal.valueOf(8050.50).compareTo(debt.getSumDebtCurrent()));
        assertEquals(accountId, debt.getAccountId());
        assertEquals(Boolean.FALSE, debt.getDebtorFlag());
        assertEquals(Boolean.TRUE, debt.getDeleteFlag());

        debtOperations = debt.getDebtOperations();
        assertEquals(0, debtOperations.size());

        /*
        debtOperation = debtOperations.get(0);
        assertEquals(Boolean.TRUE, debtOperation.getFirstOperation());
        assertEquals(Boolean.TRUE, debtOperation.getDeleteFlag());
        assertEquals(debt.getId(), debtOperation.getDebtId());
        */

        account = accountService.getAccount(accountId);
        assertEquals(0, BigDecimal.valueOf(10000.00).compareTo(account.getCurrentSum()));

        tickets = ticketService.getTickets(TicketFilterDto.builder()
                        .accountId(accountId)
                        .from(LocalDate.of(2024, 10, 1))
                        .to(LocalDate.of(2024, 10, 27))
                        .build())
                .stream().filter(item -> !item.getOperations().isEmpty()).collect(Collectors.toList());
        assertEquals(0, tickets.size());
    }

    @Test
    public void testCreateAndCloseDebtSuccess() throws Exception {
        var debtDate = LocalDate.of(2024, 10, 10);

        // создаём долг
        var accountId = getAccountId("test_create_close_debt");
        var debtId = debtService.createDebt(DebtDto.builder()
                .accountId(accountId)
                .name("Взял инвестиции для бизнеса")
                .description("Буду развивать свой проект")
                .sumDebt(BigDecimal.valueOf(8050.5))
                .createDate(debtDate)
                .debtorFlag(Boolean.TRUE)
                .build());

        var account = accountService.getAccount(accountId);
        assertEquals(0, BigDecimal.valueOf(18050.5).compareTo(account.getCurrentSum()));

        debtService.addDebtOperation(DebtOperationDto.builder()
                .debtId(debtId)
                .ticket(TicketDto.builder()
                        .totalSum(BigDecimal.valueOf(8050.5))
                        .date(LocalDate.of(2024, 10, 12))
                        .build())
                .build());

        var debt = debtService.getDebt(debtId);
        assertEquals(Boolean.FALSE, debt.getIsActive());
        assertEquals(debtDate, debt.getCreateDate());
        assertEquals(LocalDate.of(2024, 10, 12), debt.getCloseDate());
        assertEquals("Взял инвестиции для бизнеса", debt.getName());
        assertEquals("Буду развивать свой проект", debt.getDescription());
        assertEquals(0, BigDecimal.valueOf(8050.5).compareTo(debt.getSumDebt()));
        assertEquals(0, BigDecimal.ZERO.compareTo(debt.getSumDebtCurrent()));
        assertEquals(accountId, debt.getAccountId());
        assertEquals(Boolean.TRUE, debt.getDebtorFlag());
        assertEquals(Boolean.FALSE, debt.getDeleteFlag());

        account = accountService.getAccount(accountId);
        assertEquals(0, BigDecimal.valueOf(10000.00).compareTo(account.getCurrentSum()));
    }
}
