package ru.make.account.core.arving.web.dto.ticket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketsOfDayDto {
    @Builder.Default
    private List<TicketDto> tickets = new ArrayList<>();
    private BigDecimal sumOfDay;
    private LocalDate dayDate;
}
