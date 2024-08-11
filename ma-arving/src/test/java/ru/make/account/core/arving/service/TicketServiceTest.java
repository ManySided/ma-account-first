package ru.make.account.core.arving.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.make.account.core.arving.model.TicketDirectionEnum;
import ru.make.account.core.arving.web.dto.operation.CategoryDto;
import ru.make.account.core.arving.web.dto.operation.OperationDto;
import ru.make.account.core.arving.web.dto.ticket.TicketDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class TicketServiceTest extends AbstractTestBase {
    @Autowired
    TicketService ticketService;

    @Test
    public void testCreateTicketSuccess() throws Exception {
        // подготовка
        var accountBefore = getAccount();
        var ticketDate = LocalDate.of(2024, 4, 30);
        var ticketDirection = TicketDirectionEnum.EXPENDITURE;
        var operation1 = OperationDto.builder()
                .name("хлеб")
                .sum(BigDecimal.valueOf(36.36))
                .category(CategoryDto.builder()
                        .id(getCategoryProductsId())
                        .build())
                .comment("купил хлеб")
                .build();
        var operation2 = OperationDto.builder()
                .name("автобус")
                .sum(BigDecimal.valueOf(37))
                .category(
                        CategoryDto.builder()
                                .id(getCategoryTransportId())
                                .build())
                .comment("поездка за хлебом")
                .build();

        // выполнение
        var ticketCreateRequest = TicketDto.builder()
                .ticketDirection(ticketDirection)
                .date(ticketDate)
                .accountId(accountBefore.getId())
                .operations(List.of(operation1, operation2))
                .build();
        var response = ticketService.saveTicket(ticketCreateRequest);

        // проверка
        var accountAfter = getAccount();
        var differenceSum = accountBefore.getCurrentSum().subtract(accountAfter.getCurrentSum());

        assertEquals(BigDecimal.valueOf(73.36), differenceSum);
        var ticketSaved = ticketService.getTicket(response);
        assertEquals(ticketDate, ticketSaved.getDate());
        assertEquals(ticketDirection, ticketSaved.getTicketDirection());
        var operations = ticketSaved.getOperations();
        assertEquals(2, operations.size());
        assertOperation(operations.stream()
                        .filter(item -> item.getName().equals(operation1.getName()))
                        .findFirst()
                        .orElseThrow(() -> new NullPointerException("Отсутствует операция")),
                operation1);
        assertOperation(operations.stream()
                        .filter(item -> item.getName().equals(operation2.getName()))
                        .findFirst()
                        .orElseThrow(() -> new NullPointerException("Отсутствует операция")),
                operation2);
    }

    @Test
    public void testUpdateTicketSuccess() throws Exception {
        // подготовка
        var accountBefore = getAccount();
        var ticketDate = LocalDate.of(2024, 4, 30);
        var ticketDirection = TicketDirectionEnum.EXPENDITURE;
        var operation1 = OperationDto.builder()
                .name("хлеб")
                .sum(BigDecimal.valueOf(36.36))
                .category(CategoryDto.builder()
                        .id(getCategoryProductsId())
                        .build())
                .comment("купил хлеб")
                .build();
        var operation2 = OperationDto.builder()
                .name("автобус")
                .sum(BigDecimal.valueOf(37))
                .category(
                        CategoryDto.builder()
                                .id(getCategoryTransportId())
                                .build())
                .comment("поездка за хлебом")
                .build();

        // выполнение
        var ticketCreateRequest = TicketDto.builder()
                .ticketDirection(ticketDirection)
                .date(ticketDate)
                .accountId(accountBefore.getId())
                .operations(List.of(operation1, operation2))
                .build();
        var response = ticketService.saveTicket(ticketCreateRequest);
        accountBefore = getAccount();

        // смена даты чека
        var ticket = ticketService.getTicket(response);
        ticket.setDate(LocalDate.of(2024, 6, 30));
        ticketService.updateTicket(ticket);
        ticket = ticketService.getTicket(response);
        assertEquals(LocalDate.of(2024, 6, 30), ticket.getDate());
        var accountAfter = getAccount();
        assertEquals(accountBefore.getCurrentSum(), accountAfter.getCurrentSum());
        accountBefore = getAccount();

        // смена даты чека и направления чека
        ticket = ticketService.getTicket(response);
        ticket.setDate(LocalDate.of(2024, 7, 30));
        ticket.setTicketDirection(TicketDirectionEnum.INCOME);
        ticketService.updateTicket(ticket);
        ticket = ticketService.getTicket(response);
        assertEquals(LocalDate.of(2024, 7, 30), ticket.getDate());
        assertEquals(TicketDirectionEnum.INCOME, ticket.getTicketDirection());
        accountAfter = getAccount();
        var differenceSum = accountBefore.getCurrentSum().subtract(accountAfter.getCurrentSum());
        assertEquals(BigDecimal.valueOf(146.72).negate(), differenceSum);
    }

    @Test
    public void testUpdateOperationOfTicketSuccess() throws Exception {
        // подготовка
        var accountBefore = getAccount();
        var ticketDate = LocalDate.of(2024, 4, 30);
        var ticketDirection = TicketDirectionEnum.EXPENDITURE;
        var operation1 = OperationDto.builder()
                .name("хлеб")
                .sum(BigDecimal.valueOf(36.36))
                .category(CategoryDto.builder()
                        .id(getCategoryProductsId())
                        .build())
                .comment("купил хлеб")
                .build();
        var operation2 = OperationDto.builder()
                .name("автобус")
                .sum(BigDecimal.valueOf(37))
                .category(
                        CategoryDto.builder()
                                .id(getCategoryTransportId())
                                .build())
                .comment("поездка за хлебом")
                .build();

        var ticketCreateRequest = TicketDto.builder()
                .ticketDirection(ticketDirection)
                .date(ticketDate)
                .accountId(accountBefore.getId())
                .operations(List.of(operation1, operation2))
                .build();
        var response = ticketService.saveTicket(ticketCreateRequest);

        // смена даты чека
        var ticket = ticketService.getTicket(response);
        var operation = ticket.getOperations().stream()
                .filter(item -> item.getName().equals("хлеб"))
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Операция не найдена"));
        operation.setName("Крутой хлебушек");
        operation.setSum(BigDecimal.valueOf(50.8));
        operation.setComment(null);
        ticketService.updateOperationOfTicket(operation);

        // проверка
        ticket = ticketService.getTicket(response);
        var account = getAccount();
        assertEquals("9912.20", account.getCurrentSum().toString());
        var operationOpt = ticket.getOperations().stream()
                .filter(item -> item.getName().equals("хлеб"))
                .findFirst();
        assertFalse(operationOpt.isPresent());
        operationOpt = ticket.getOperations().stream()
                .filter(item -> item.getName().equals("Крутой хлебушек"))
                .findFirst();
        assertEquals("Крутой хлебушек", operationOpt.get().getName());
        assertEquals("50.80", operationOpt
                .map(OperationDto::getSum)
                .map(BigDecimal::toString)
                .orElse(null));
        assertNull(operationOpt.get().getComment());

    }

    @Test
    public void testRemoveTicketSuccess() throws Exception {
        var accountBefore = getAccount();
        var ticketDate = LocalDate.of(2024, 4, 30);
        var ticketDirection = TicketDirectionEnum.EXPENDITURE;
        var operation1 = OperationDto.builder()
                .name("хлеб")
                .sum(BigDecimal.valueOf(36.36))
                .category(CategoryDto.builder()
                        .id(getCategoryProductsId())
                        .build())
                .comment("купил хлеб")
                .build();
        var operation2 = OperationDto.builder()
                .name("автобус")
                .sum(BigDecimal.valueOf(37))
                .category(
                        CategoryDto.builder()
                                .id(getCategoryTransportId())
                                .build())
                .comment("поездка за хлебом")
                .build();

        var ticketCreateRequest = TicketDto.builder()
                .ticketDirection(ticketDirection)
                .date(ticketDate)
                .accountId(accountBefore.getId())
                .operations(List.of(operation1, operation2))
                .build();
        var response = ticketService.saveTicket(ticketCreateRequest);

        // удаление
        ticketService.removeTicket(response);

        // получение удалённого чека
        var ticket = ticketService.getTicket(response);
        assertEquals(0, ticket.getOperations().size());
        var account = getAccount();
        assertEquals(accountBefore.getCurrentSum(), account.getCurrentSum());
    }

    @Test
    public void testRemoveOperationOfTicketSuccess() throws Exception {
        var accountBefore = getAccount();
        var ticketDate = LocalDate.of(2024, 4, 30);
        var ticketDirection = TicketDirectionEnum.EXPENDITURE;
        var operation1 = OperationDto.builder()
                .name("хлеб")
                .sum(BigDecimal.valueOf(36.36))
                .category(CategoryDto.builder()
                        .id(getCategoryProductsId())
                        .build())
                .comment("купил хлеб")
                .build();
        var operation2 = OperationDto.builder()
                .name("автобус")
                .sum(BigDecimal.valueOf(37))
                .category(
                        CategoryDto.builder()
                                .id(getCategoryTransportId())
                                .build())
                .comment("поездка за хлебом")
                .build();

        var ticketCreateRequest = TicketDto.builder()
                .ticketDirection(ticketDirection)
                .date(ticketDate)
                .accountId(accountBefore.getId())
                .operations(List.of(operation1, operation2))
                .build();
        var response = ticketService.saveTicket(ticketCreateRequest);

        // удаление
        var ticketCreated = ticketService.getTicket(response);
        var operation = ticketCreated.getOperations().stream()
                .filter(item -> item.getName().equals("автобус"))
                .findFirst()
                .orElseThrow(() -> new NullPointerException());
        ticketService.removeOperationOfTicket(operation.getId());

        // провека
        ticketCreated = ticketService.getTicket(response);
        assertEquals(1, ticketCreated.getOperations().size());
        assertEquals("хлеб", ticketCreated.getOperations().get(0).getName());
        var account = getAccount();
        assertEquals("9963.64", account.getCurrentSum().toString());
    }

    private void assertOperation(OperationDto actual, OperationDto request) {
        assertEquals(request.getCategory().getId(), actual.getCategory().getId());
        assertEquals(request.getName(), actual.getName());
        assertEquals(request.getComment(), actual.getComment());
        assertThat(request.getSum()).isEqualByComparingTo(actual.getSum());
        assertEquals(Boolean.TRUE, actual.getIsActive());
        assertEquals(Boolean.FALSE, actual.getStuffFlag());
    }
}
