package ru.make.account.core.arving.web.dto.ticket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.make.account.core.arving.model.TicketDirectionEnum;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketFilterDto {
    private LocalDate from;
    private LocalDate to;
    private Long accountId;
    List<TicketDirectionEnum> directions;
}
