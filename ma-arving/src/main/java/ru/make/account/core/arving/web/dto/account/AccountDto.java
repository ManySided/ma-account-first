package ru.make.account.core.arving.web.dto.account;

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
    private String name;
    private String comment;
    private BigDecimal startSum;
    private BigDecimal currentSum;
    private CurrencyDto currency;
    private LocalDateTime created;
    private Boolean actual;
}
