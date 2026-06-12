package ru.make.account.core.arving.service.upload;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.make.account.core.arving.exception.ProcessException;
import ru.make.account.core.arving.model.TicketDirectionEnum;
import ru.make.account.core.arving.security.SecurityHandler;
import ru.make.account.core.arving.service.AccountService;
import ru.make.account.core.arving.service.CategoryService;
import ru.make.account.core.arving.service.OperationTagService;
import ru.make.account.core.arving.service.TicketService;
import ru.make.account.core.arving.web.dto.category.CategoryDto;
import ru.make.account.core.arving.web.dto.operation.OperationDto;
import ru.make.account.core.arving.web.dto.ticket.TicketDto;
import ru.make.account.core.arving.web.dto.upload.StructureRecordDto;
import ru.make.account.core.arving.web.dto.upload.UploadCsvResponseDto;
import ru.make.account.core.arving.web.dto.upload.UploadRecordDto;

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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImportDataService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final SecurityHandler securityHandler;
    private final CategoryService categoryService;
    private final TicketService ticketService;
    private final AccountService accountService;
    private final OperationTagService operationTagService;

    @Transactional
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
                var operationComment = array[4];
                var categoryName = array[5];
                var tagName = array[6];

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
                operation.setImportFlag(Boolean.TRUE);
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

    public UploadCsvResponseDto prepareModel(Long accountId, InputStream stream) {
        UploadCsvResponseDto uploadResponse = UploadCsvResponseDto.builder()
                .accountId(accountId)
                .statusLoad(Boolean.TRUE)
                .records(new ArrayList<>())
                .build();
        try (InputStreamReader streamReader = new InputStreamReader(stream, StandardCharsets.UTF_8);
             var bufferedReader = new BufferedReader(streamReader)) {

            LocalDate currentDate = null;

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                var array = line.split("[|]");

                var date = array[0];
                var sumMinus = array[1];
                var sumPlus = array[2];
                var operationName = array[3];
                var operationComment = array[4];
                var categoryName = array[5];
                var tagName = array[6];

                if (StringUtils.isNotEmpty(date))
                    currentDate = convert(date);

                var builder = UploadRecordDto.builder()
                        .date(currentDate)
                        .sum(StringUtils.isNotEmpty(sumMinus)
                                ? convertNumber(sumMinus)
                                : convertNumber(sumPlus))
                        .ticketDirection(StringUtils.isNotEmpty(sumMinus)
                                ? TicketDirectionEnum.EXPENDITURE
                                : TicketDirectionEnum.INCOME)
                        .name(operationName)
                        .comment(operationComment);
                var categoryDb = categoryService.getOneCategoryByName(accountId, categoryName);
                if (Objects.nonNull(categoryDb)) {
                    builder.category(StructureRecordDto.builder()
                            .id(categoryDb.getId())
                            .name(categoryDb.getName())
                            .build());
                } else {
                    builder.category(StructureRecordDto.builder()
                            .name(categoryName)
                            .build());
                }
                if (StringUtils.isNotEmpty(tagName)) {
                    var operationTag = operationTagService.getTagByName(accountId, tagName);
                    if (Objects.nonNull(operationTag))
                        builder.tagRecord(StructureRecordDto.builder()
                                .id(operationTag.getId())
                                .name(operationTag.getName())
                                .build());
                    else builder.tagRecord(StructureRecordDto.builder()
                            .name(tagName)
                            .build());
                }
                uploadResponse.getRecords().add(builder.build());
            }
        } catch (Exception e) {
            return UploadCsvResponseDto.builder()
                    .statusLoad(Boolean.FALSE)
                    .errorText("Ошибка парсинга файла")
                    .accountId(accountId)
                    .build();
        }
        return uploadResponse;
    }

    @Transactional
    public Boolean saveModel(UploadCsvResponseDto model) {
        var records = model.getRecords();
        var mapByDirection = records.stream()
                .collect(Collectors.groupingBy(UploadRecordDto::getTicketDirection));
        for (var ticketDirectionEntry : mapByDirection.entrySet()) {
            var direction = ticketDirectionEntry.getKey();
            var operations = ticketDirectionEntry.getValue();
            var mapDateOperation = operations.stream()
                    .collect(Collectors.groupingBy(UploadRecordDto::getDate));

            for (var dateOperationListEntry : mapDateOperation.entrySet()) {
                ticketService.saveTicket(TicketDto.builder()
                        .date(dateOperationListEntry.getKey())
                        .accountId(model.getAccountId())
                        .ticketDirection(direction)
                        .user(securityHandler.getUserId())
                        .operations(dateOperationListEntry.getValue().stream()
                                .map(this::createOperation)
                                .collect(Collectors.toList()))
                        .build());
            }
        }
        return Boolean.TRUE;
    }

    private OperationDto createOperation(UploadRecordDto uploadRecord) {
        var category = categoryService.getCategoryById(uploadRecord.getCategory().getId());
        var tagOpt = Optional.of(uploadRecord).map(UploadRecordDto::getTagRecord);
        var tags = tagOpt
                .map(StructureRecordDto::getId)
                .map(operationTagService::getTagById)
                .map(List::of)
                .orElse(null);
        return OperationDto.builder()
                .sum(uploadRecord.getSum())
                .name(uploadRecord.getName())
                .comment(uploadRecord.getComment())
                .category(category)
                .tags(tags)
                .isActive(Boolean.TRUE)
                .importFlag(Boolean.TRUE)
                .build();
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
