package ru.make.account.core.arving.web.dto.debt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.make.account.core.arving.web.dto.ticket.TicketDto;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DebtOperationDto {
    private Long id;
    private Boolean firstOperation;
    private TicketDto ticket;
    private Long debtId;
    private Boolean deleteFlag;
    private BigDecimal sumOperation;
}
