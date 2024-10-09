package ru.make.account.core.arving.web.dto.debt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.make.account.core.arving.web.dto.ticket.TicketDto;

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
}
