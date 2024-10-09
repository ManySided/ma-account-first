package ru.make.account.core.arving.web.dto.debt;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DebtDto {
    private Long id;
    private Boolean isActive;
    private LocalDate createDate;
    private LocalDate closeDate;
    @NotNull(message = "Поле 'Название долга' должно быть заполнен")
    private String name;
    private String description;
    @NotNull(message = "Поле 'Сумма долга' должно быть заполнен")
    private BigDecimal sumDebt;
    private BigDecimal sumDebtCurrent;
    @NotNull(message = "Поле 'Счёт по долгу' должно быть заполнен")
    private Long accountId;
    @NotNull(message = "Поле 'Флаг должник' должно быть заполнен")
    private Boolean debtorFlag;
    private Boolean deleteFlag;
    private List<DebtOperationDto> debtOperations;
}
