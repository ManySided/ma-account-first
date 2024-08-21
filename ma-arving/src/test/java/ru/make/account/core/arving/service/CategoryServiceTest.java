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
import ru.make.account.core.arving.web.dto.ticket.TicketFilterDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class CategoryServiceTest extends AbstractTestBase {
    @Autowired
    CategoryService categoryService;

    @Autowired
    TicketService ticketService;

    @Test
    public void testCreateUpdateCategorySuccess() throws Exception {
        var accountId = getAccountId("test_create_update_category");
        var parent = categoryService.saveCategory(CategoryDto.builder()
                .name("Продукты питания")
                .accountId(accountId)
                .build());
        var child1 = categoryService.saveCategory(CategoryDto.builder()
                .name("Пироги")
                .accountId(accountId)
                .parent(parent)
                .build());
        var child2 = categoryService.saveCategory(CategoryDto.builder()
                .name("Мороженное")
                .accountId(accountId)
                .parent(parent)
                .build());

        // проверяем сохранённые категории
        var categoryTreeByAccountId = categoryService.getCategoryTreeByAccountId(accountId);
        assertEquals(1, categoryTreeByAccountId.size());
        assertEquals("Продукты питания", categoryTreeByAccountId.get(0).getName());
        assertEquals(Boolean.FALSE, categoryTreeByAccountId.get(0).getStuffFlag());
        assertEquals(2, categoryTreeByAccountId.get(0).getSubCategories().size());
        assertTrue(categoryTreeByAccountId.get(0).getSubCategories().stream().anyMatch(item -> item.getName().equals("Пироги")));
        assertTrue(categoryTreeByAccountId.get(0).getSubCategories().stream().anyMatch(item -> item.getName().equals("Мороженное")));

        // переименуем категорию
        categoryService.saveCategory(CategoryDto.builder()
                .id(parent)
                .name("Сладкое")
                .accountId(accountId)
                .build());

        // проверка, что переименовалось
        categoryTreeByAccountId = categoryService.getCategoryTreeByAccountId(accountId);
        assertEquals(1, categoryTreeByAccountId.size());
        assertEquals("Сладкое", categoryTreeByAccountId.get(0).getName());
        assertEquals(Boolean.FALSE, categoryTreeByAccountId.get(0).getStuffFlag());
        assertEquals(2, categoryTreeByAccountId.get(0).getSubCategories().size());
    }

    @Test
    public void testDeleteCategorySuccess() throws Exception {
        var accountId = getAccountId("test_delete_category");
        var parent = categoryService.saveCategory(CategoryDto.builder()
                .name("Продукты питания")
                .accountId(accountId)
                .build());
        var child1 = categoryService.saveCategory(CategoryDto.builder()
                .name("Пироги")
                .accountId(accountId)
                .parent(parent)
                .build());
        var child2 = categoryService.saveCategory(CategoryDto.builder()
                .name("Мороженное")
                .accountId(accountId)
                .parent(parent)
                .build());
        // добавляем на категорию операцию
        var ticketDate = LocalDate.of(2024, 4, 30);
        var ticketDirection = TicketDirectionEnum.EXPENDITURE;
        var operation1 = OperationDto.builder()
                .name("пирог вишнёвый")
                .sum(BigDecimal.valueOf(36.36))
                .category(CategoryDto.builder()
                        .id(child1)
                        .build())
                .comment("сладкий")
                .build();
        var ticketId = ticketService.saveTicket(TicketDto.builder()
                .ticketDirection(ticketDirection)
                .date(ticketDate)
                .accountId(accountId)
                .operations(List.of(operation1))
                .build());


        // проверяем сохранённые категории
        var categoryTreeByAccountId = categoryService.getCategoryTreeByAccountId(accountId);
        assertEquals(1, categoryTreeByAccountId.size());
        assertEquals("Продукты питания", categoryTreeByAccountId.get(0).getName());
        assertEquals(Boolean.FALSE, categoryTreeByAccountId.get(0).getStuffFlag());
        assertEquals(2, categoryTreeByAccountId.get(0).getSubCategories().size());
        assertTrue(categoryTreeByAccountId.get(0).getSubCategories().stream().anyMatch(item -> item.getName().equals("Пироги")));
        assertTrue(categoryTreeByAccountId.get(0).getSubCategories().stream().anyMatch(item -> item.getName().equals("Мороженное")));
        // проверяем категорию операции
        var tickets = ticketService.getTickets(TicketFilterDto.builder()
                .accountId(accountId)
                .from(LocalDate.of(2000, 1, 1))
                .to(LocalDate.of(2050, 1, 1))
                .build());
        var ticket = tickets.get(0);
        var operation = ticket.getOperations().get(0);
        assertEquals(child1, operation.getCategory().getId());

        // удаляем категорию
        categoryService.deleteCategory(child1, child2);

        // проверяем, что категория не выводится
        categoryTreeByAccountId = categoryService.getCategoryTreeByAccountId(accountId);
        assertEquals(1, categoryTreeByAccountId.size());
        assertEquals("Продукты питания", categoryTreeByAccountId.get(0).getName());
        assertEquals(Boolean.FALSE, categoryTreeByAccountId.get(0).getStuffFlag());
        assertEquals(1, categoryTreeByAccountId.get(0).getSubCategories().size());
        assertFalse(categoryTreeByAccountId.get(0).getSubCategories().stream().anyMatch(item -> item.getName().equals("Пироги")));
        assertTrue(categoryTreeByAccountId.get(0).getSubCategories().stream().anyMatch(item -> item.getName().equals("Мороженное")));
        // проверяем категорию операции
        tickets = ticketService.getTickets(TicketFilterDto.builder()
                .accountId(accountId)
                .from(LocalDate.of(2000, 1, 1))
                .to(LocalDate.of(2050, 1, 1))
                .build());
        ticket = tickets.get(0);
        operation = ticket.getOperations().get(0);
        assertEquals(child2, operation.getCategory().getId());
    }
}
