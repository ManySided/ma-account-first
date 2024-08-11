package ru.make.account.core.arving.service.upload;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.make.account.core.arving.exception.ProcessException;
import ru.make.account.core.arving.model.TicketDirectionEnum;
import ru.make.account.core.arving.security.SecurityHandler;
import ru.make.account.core.arving.service.AccountService;
import ru.make.account.core.arving.service.CategoryService;
import ru.make.account.core.arving.service.TicketService;
import ru.make.account.core.arving.web.dto.operation.CategoryDto;
import ru.make.account.core.arving.web.dto.operation.OperationDto;
import ru.make.account.core.arving.web.dto.ticket.TicketDto;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImportDataService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final SecurityHandler securityHandler;
    private final CategoryService categoryService;
    private final TicketService ticketService;
    private final AccountService accountService;

    public Boolean importData(Long accountId, InputStream stream) {
        try (InputStreamReader streamReader = new InputStreamReader(stream, StandardCharsets.UTF_8);
             var bufferedReader = new BufferedReader(streamReader)) {

            TicketDto ticket = null;
            List<OperationDto> minus = new ArrayList<>();
            List<OperationDto> plus = new ArrayList<>();

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                var array = line.split("[|]");

                var date = array[0];
                var sumMinus = array[1];
                var sumPlus = array[2];
                var operationName = array[3];
                var categoryName = array[4];

                if (nonNull(date)) {
                    if (ticket == null) {
                        var account = accountService.getAccount(accountId);
                        ticket = new TicketDto();
                        ticket.setAccountId(account.getId());
                        ticket.setDate(convert(date));
                        ticket.setUser(securityHandler.getUserId());
                    } else {
                        if (!minus.isEmpty()) {
                            ticket.setTicketDirection(TicketDirectionEnum.EXPENDITURE);
                            ticket.setOperations(minus);
                            ticketService.saveTicket(ticket);
                            minus = new ArrayList<>();
                        }

                        if (!plus.isEmpty()) {
                            ticket.setTicketDirection(TicketDirectionEnum.INCOME);
                            ticket.setOperations(plus);
                            ticketService.saveTicket(ticket);
                            plus = new ArrayList<>();
                        }

                        var account = accountService.getAccount(accountId);
                        ticket = new TicketDto();
                        ticket.setAccountId(account.getId());
                        ticket.setDate(convert(date));
                        ticket.setUser(securityHandler.getUserId());
                    }
                }

                OperationDto operation = new OperationDto();
                operation.setIsActive(Boolean.TRUE);
                operation.setCategory(getCategory(accountId, categoryName));
                operation.setName(operationName);
                if (nonNull(sumMinus)) {
                    operation.setSum(convertNumber(sumMinus));
                    minus.add(operation);
                }
                if (nonNull(sumPlus)) {
                    operation.setSum(convertNumber(sumPlus));
                    plus.add(operation);
                }
            }

            if (!minus.isEmpty()) {
                ticket.setTicketDirection(TicketDirectionEnum.EXPENDITURE);
                ticket.setOperations(minus);
                ticketService.saveTicket(ticket);
            }

            if (!plus.isEmpty()) {
                ticket.setTicketDirection(TicketDirectionEnum.INCOME);
                ticket.setOperations(plus);
                ticketService.saveTicket(ticket);
            }
        } catch (Exception e) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    private CategoryDto getCategory(Long accountId, String name) {
        var categories = categoryService.getCategory(accountId, name);
        if (categories.isEmpty())
            throw new ProcessException("Категория не найдена");
        if (categories.size() > 1)
            throw new ProcessException("Найдено более одной категории [" + name + "]");
        return categories.get(0);
    }

    private boolean nonNull(String value) {
        return Objects.nonNull(value) && !value.isEmpty() && !value.isBlank();
    }

    private LocalDate convert(String date) {
        return LocalDate.parse(date, FORMATTER);
    }

    private BigDecimal convertNumber(String value) {
        if (nonNull(value)) {
            if (value.contains(",")) {
                return new BigDecimal(value.replace(",", "."));
            } else return new BigDecimal(value);
        }
        return null;
    }
}
