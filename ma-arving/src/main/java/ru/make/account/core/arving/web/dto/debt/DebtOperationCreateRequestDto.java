package ru.make.account.core.arving.web.dto.debt;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DebtOperationCreateRequestDto {
    @NotNull(message = "Поле 'Идентификатор долга' должно быть заполнен")
    private Long debtId;
    @NotNull(message = "Поле 'Дата операции' должно быть заполнен")
    private LocalDate date;
    @NotNull(message = "Поле 'Сумма операции' должно быть заполнен")
    private BigDecimal sum;
}
