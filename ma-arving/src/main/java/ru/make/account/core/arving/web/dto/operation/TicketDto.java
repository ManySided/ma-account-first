package ru.make.account.core.arving.web.dto.operation;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.make.account.core.arving.model.TicketDirectionEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketDto {
    private Long id;
    @NotNull(message = "Поле 'Тип чека' должно быть заполнен")
    private TicketDirectionEnum ticketDirection;
    @NotNull(message = "Поле 'Дата чека' должно быть заполнен")
    private LocalDate date;
    @NotNull(message = "Поле 'Счёт' должно быть заполнен")
    private Long accountId;
    private UUID user;
    private BigDecimal discount;
    @NotNull(message = "Поле 'Операции чека' должно быть заполнен")
    @NotEmpty(message = "Поле 'Операции чека' должно быть заполнен")
    private List<OperationDto> operations;
}
