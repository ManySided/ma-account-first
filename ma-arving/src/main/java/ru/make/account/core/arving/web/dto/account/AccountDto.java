package ru.make.account.core.arving.web.dto.account;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.make.account.core.arving.web.dto.currency.CurrencyDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    private Long id;
    @NotBlank(message = "Поле 'Название счёта' должно быть заполнен")
    private String name;
    private String comment;
    @NotNull(message = "Поле 'Начальная сумма' должно быть заполнен")
    @Min(value = 0, message = "Поле 'Начальная сумма' должно быть больше нуля")
    private BigDecimal startSum;
    private BigDecimal currentSum;
    @NotNull(message = "Поле 'Валюта счёта' должно быть заполнен")
    private CurrencyDto currency;
    private LocalDateTime created;
    private Boolean actual;
}
