package ru.make.account.core.arving.web.dto.upload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.make.account.core.arving.model.TicketDirectionEnum;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadRecordDto {
    private LocalDate date;
    private BigDecimal sum;
    private TicketDirectionEnum ticketDirection;
    private String name;
    private String comment;
    private StructureRecordDto category;
    private StructureRecordDto tagRecord;
}
