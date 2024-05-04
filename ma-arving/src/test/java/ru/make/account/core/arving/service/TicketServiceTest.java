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
import ru.make.account.core.arving.web.dto.operation.TicketDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

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

    private void assertOperation(OperationDto actual, OperationDto request) {
        assertEquals(request.getCategory().getId(), actual.getCategory().getId());
        assertEquals(request.getName(), actual.getName());
        assertEquals(request.getComment(), actual.getComment());
        assertThat(request.getSum()).isEqualByComparingTo(actual.getSum());
        assertEquals(Boolean.TRUE, actual.getIsActive());
        assertEquals(Boolean.FALSE, actual.getStuffFlag());
    }
}
